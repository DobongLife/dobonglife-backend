import fetch from "node-fetch";
import Anthropic from "@anthropic-ai/sdk";

const {
    ANTHROPIC_API_KEY,
    GITHUB_TOKEN,
    GITHUB_REPOSITORY,
    PR_NUMBER,
} = process.env;

if (!ANTHROPIC_API_KEY || !GITHUB_TOKEN || !GITHUB_REPOSITORY || !PR_NUMBER) {
    throw new Error("Missing env: ANTHROPIC_API_KEY/GITHUB_TOKEN/GITHUB_REPOSITORY/PR_NUMBER");
}

const [owner, repo] = GITHUB_REPOSITORY.split("/");

const gh = async (path, { method = "GET", body } = {}) => {
    const res = await fetch(`https://api.github.com${path}`, {
        method,
        headers: {
            Authorization: `Bearer ${GITHUB_TOKEN}`,
            "Content-Type": "application/json",
            Accept: "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
        },
        body: body ? JSON.stringify(body) : undefined,
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`GitHub API error ${res.status} on ${path}: ${text}`);
    }
    return res.json();
};

function clampReviewComments(comments, max = 25) {
    return comments.slice(0, max);
}

function safeJsonParse(text) {
    const first = text.indexOf("{");
    const last = text.lastIndexOf("}");
    if (first === -1 || last === -1 || last <= first) throw new Error("No JSON object found in Claude output");
    return JSON.parse(text.slice(first, last + 1));
}

function formatScoreBreakdown(bd) {
    if (!bd || typeof bd !== "object") return "";
    const keys = Object.keys(bd);
    if (!keys.length) return "";
    return keys.map(k => `- ${k}: ${bd[k]}`).join("\n");
}

const anthropic = new Anthropic({ apiKey: ANTHROPIC_API_KEY });

async function main() {
    // 1) PR 정보(헤드 SHA 필요)
    const pr = await gh(`/repos/${owner}/${repo}/pulls/${PR_NUMBER}`);
    const headSha = pr.head.sha;

    // 2) PR 파일 목록(+patch)
    let page = 1;
    const files = [];
    while (true) {
        const batch = await gh(`/repos/${owner}/${repo}/pulls/${PR_NUMBER}/files?per_page=100&page=${page}`);
        files.push(...batch);
        if (batch.length < 100) break;
        page++;
    }

    // patch 없는 경우(큰 diff 등) 제외(라인코멘트 불가)
    const changed = files
        .filter(f => f.patch && typeof f.patch === "string")
        .map(f => ({
            filename: f.filename,
            status: f.status,
            additions: f.additions,
            deletions: f.deletions,
            patch: f.patch,
        }));

    // 3) Claude 호출
    const systemPrompt =
        "너는 보안 관점까지 포함해서 엄격하게 리뷰하는 수석(Principal) 백엔드 엔지니어다. " +
        "반드시 유효한 JSON만 출력해라. JSON 외의 설명/마크다운 펜스/추가 텍스트는 출력하지 마라.";

    const userPrompt = buildJavaSpringPromptKo({ pr, changed });

    const resp = await anthropic.messages.create({
        model: "claude-3-5-sonnet-20241022",
        max_tokens: 2500,
        temperature: 0.2,
        system: systemPrompt,
        messages: [{ role: "user", content: userPrompt }],
    });

    const text = resp.content?.[0]?.text ?? "";
    const out = safeJsonParse(text);

    const inline = Array.isArray(out.inline_comments) ? out.inline_comments : [];
    const prReviewComments = clampReviewComments(
        inline
            .filter(c => c && c.path && Number.isInteger(c.line) && c.body)
            .map(c => ({
                path: c.path,
                line: c.line, // 새 파일 기준 라인 번호
                side: c.side === "LEFT" ? "LEFT" : "RIGHT",
                body: String(c.body).slice(0, 65000),
            }))
    );

    const reviewBodyParts = [];
    if (out.summary) reviewBodyParts.push(`## PR 요약\n${out.summary}`);
    if (out.score?.total != null) {
        reviewBodyParts.push(
            `## 리뷰 점수\n**총점: ${out.score.total}/100**\n\n` +
            formatScoreBreakdown(out.score.breakdown) +
            (out.score.notes ? `\n\n${out.score.notes}` : "")
        );
    }
    if (out.review_body) reviewBodyParts.push(`## 리뷰 코멘트\n${out.review_body}`);

    // 👉 총점이나 치명 이슈 기준으로 "REQUEST_CHANGES"로 바꿀 수도 있음
    const event = "COMMENT";

    await gh(`/repos/${owner}/${repo}/pulls/${PR_NUMBER}/reviews`, {
        method: "POST",
        body: {
            commit_id: headSha,
            event,
            body: reviewBodyParts.join("\n\n"),
            comments: prReviewComments,
        },
    });

    console.log(`✅ 한국어 리뷰 등록 완료 (inline ${prReviewComments.length}개)`);
}

function buildJavaSpringPromptKo({ pr, changed }) {
    const header = `
다음 GitHub Pull Request를 Java/Spring 백엔드 관점에서 엄격하게 코드리뷰 해줘.

PR 정보:
- 제목: ${pr.title}
- base: ${pr.base.ref}
- head: ${pr.head.ref}
- 작성자: ${pr.user?.login ?? "unknown"}

반드시 아래 "정확한 JSON 스키마"로만 출력해.
JSON 외의 텍스트는 절대 출력하지 마.

출력 JSON 스키마:
{
  "summary": "3~6개 불릿으로 PR 요약(한국어)",
  "score": {
    "total": 0-100,
    "breakdown": {
      "정확성(0-5)": 0,
      "보안(0-5)": 0,
      "성능(0-5)": 0,
      "유지보수성(0-5)": 0,
      "테스트품질(0-5)": 0,
      "스프링베스트프랙티스(0-5)": 0
    },
    "notes": "점수 근거 1~3문장(한국어)"
  },
  "review_body": "마크다운 허용(한국어). 핵심 이슈/대안/수정 제안 중심",
  "inline_comments": [
    { "path": "파일경로", "line": 123, "side": "RIGHT", "body": "한국어 코멘트" }
  ]
}

inline_comments 규칙(중요):
- 기본은 새 파일 기준 라인 번호로 코멘트를 달아야 하니 side는 RIGHT를 사용해.
- line은 반드시 '새 파일의 실제 라인 번호(정수)' 여야 해.
- path는 아래에 제공되는 FILE의 filename과 정확히 일치해야 해.
- body는 짧고 실행 가능하게(최대 8줄 정도).
- 고품질 코멘트 위주로 최대 25개까지만.

점수 규칙:
- breakdown 각 항목은 0..5 정수.
- total(0..100)은 아래 가중치로 환산해서 계산해:
  정확성 25%, 보안 20%, 성능 15%, 유지보수성 15%, 테스트품질 15%, 스프링베스트프랙티스 10%

Java/Spring 체크포인트:
- 트랜잭션 경계(@Transactional), 격리/전파, 롤백 조건
- JPA N+1, Lazy 로딩, fetch join/batch size
- Validation(@Valid), 예외 처리(@ControllerAdvice), HTTP status
- 보안: 인증/인가 누락, 입력 검증, SQLi/SSRF/로그 인젝션, 민감정보 로그(PII)
- 동시성/멱등성, 재시도, 타임아웃
- 레이어링(Controller/Service/Repo), DTO/Entity 분리, 매핑
- 설정/프로파일/시크릿 관리
- 로깅/관측성(추적ID, 구조화 로그)
`;

    const patches = changed
        .slice(0, 40)
        .map(f => `
FILE: ${f.filename}
STATUS: ${f.status} (+${f.additions}/-${f.deletions})
PATCH:
${f.patch}
`)
        .join("\n\n");

    return `${header}\n\n변경 파일 패치들:\n${patches}`;
}

main().catch(err => {
    console.error("❌ Failed:", err);
    process.exit(1);
});