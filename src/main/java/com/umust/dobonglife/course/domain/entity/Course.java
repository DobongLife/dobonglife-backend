package com.umust.dobonglife.course.domain.entity;

import com.umust.dobonglife.course.domain.constant.CourseLevel;
import com.umust.dobonglife.course.domain.constant.CourseTheme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = true)
    private String subTitle;
    @Column(nullable = false)
    @ElementCollection
    private List<CourseTheme> themes;
    @Column(nullable = false)
    private Double duration;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> tags;

    @Column(nullable = false)
    @ElementCollection
    @CollectionTable(name = "course_images", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> imageUrls;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private String meetingPlace;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = true)
    private String cost;
    @Column(nullable = false)
    private Integer maxNum;
    @Column(nullable = false)
    private String ageLimit;
    @Column(columnDefinition = "TEXT")
    private String cancelPolicy;
    @Column(columnDefinition = "TEXT")
    private String weatherPolicy;

    @Column(nullable = true)
    @ElementCollection // @Convert(converter = StringListConverter.class)
    @CollectionTable(name = "course_highLights", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> highlights;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_exclusions", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> exclusions;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_inclusions", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> inclusions;

    public Course(String title, String subTitle, List<CourseTheme> themes, Double duration, CourseLevel level, List<String> tags, List<String> imageUrls, String content, String meetingPlace, String contact, String cost, Integer maxNum, String ageLimit, String cancelPolicy, String weatherPolicy, List<String> highlights, List<String> exclusions, List<String> inclusions) {
        this.title = title;
        this.subTitle = subTitle;
        this.themes = themes;
        this.duration = duration;
        this.level = level;
        this.tags = tags;
        this.imageUrls = imageUrls;
        this.content = content;
        this.meetingPlace = meetingPlace;
        this.contact = contact;
        this.cost = cost;
        this.maxNum = maxNum;
        this.ageLimit = ageLimit;
        this.cancelPolicy = cancelPolicy;
        this.weatherPolicy = weatherPolicy;
        this.highlights = highlights;
        this.exclusions = exclusions;
        this.inclusions = inclusions;
    }

    public static Course create(String title, String subTitle, List<CourseTheme> themes, Double duration, CourseLevel level, List<String> tags, List<String> imageUrls, String content, String meetingPlace, String contact, String cost, int maxNum, String ageLimit, String cancelPolicy, String weatherPolicy, List<String> highlights, List<String> exclusions, List<String> inclusions) {
        validateTitle(title);
        validateDuration(duration);
        validateLevel(level);
        validateTag(tags);
        validateImageUrl(imageUrls);
        validateContent(content);
        validateMeetingPlace(meetingPlace);
        validateContact(contact);
        validateCost(cost);
        validateMaxNum(maxNum);
        validateAgeLimit(ageLimit);
        validateCancelPolicy(cancelPolicy);
        validateWeatherPolicy(weatherPolicy);
        validateHighlights(highlights);
        validateExclusions(exclusions);
        validateInclusions(inclusions);

        return new Course(title, subTitle, themes, duration, level, tags, imageUrls, content, meetingPlace, contact, cost, maxNum, ageLimit, cancelPolicy, weatherPolicy, highlights, exclusions, inclusions);
    }

    // 검증 메서드

    /**
     * 제목 검증
     * - 필수 값
     * - 공백 불가
     * - 최대 100자
     */
    private static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목은 필수입니다");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("제목은 100자를 초과할 수 없습니다");
        }
    }

    /**
     * 부제목 검증 (선택)
     * - null 허용
     * - 값이 있으면 최대 200자
     */
    private static void validateSubTitle(String subTitle) {
        if (subTitle != null && subTitle.length() > 200) {
            throw new IllegalArgumentException("부제목은 200자를 초과할 수 없습니다");
        }
    }

    /**
     * 강좌 소요시간 검증
     * - 필수 값
     * - 0보다 큰 양수
     * - 최대 24시간
     */
    private static void validateDuration(Double duration) {
        if (duration == null) {
            throw new IllegalArgumentException("소요시간은 필수입니다");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("소요시간은 0보다 커야 합니다");
        }
        if (duration > 24.0) {
            throw new IllegalArgumentException("소요시간은 24시간을 초과할 수 없습니다");
        }
    }

    /**
     * 레벨 검증
     * - 필수 값
     */
    private static void validateLevel(CourseLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("레벨은 필수입니다");
        }
    }

    /**
     * 태그 검증
     * - null 허용 (선택)
     * - 최대 5개
     * - 각 태그는 최대 20자
     */
    private static void validateTag(List<String> tag) {
        if (tag == null) {
            return; // null 허용
        }
        if (tag.size() > 5) {
            throw new IllegalArgumentException("태그는 최대 5개까지 등록할 수 있습니다");
        }
        for (String t : tag) {
            if (t == null || t.trim().isEmpty()) {
                throw new IllegalArgumentException("빈 태그는 등록할 수 없습니다");
            }
            if (t.length() > 20) {
                throw new IllegalArgumentException("각 태그는 20자를 초과할 수 없습니다");
            }
        }
    }

    /**
     * 이미지 URL 검증
     * - 필수 (최소 1개)
     * - 최대 5개
     * - URL 형식 검증
     */
    private static void validateImageUrl(List<String> imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("이미지는 최소 1개 이상 필수입니다");
        }
        if (imageUrl.size() > 5) {
            throw new IllegalArgumentException("이미지는 최대 5개까지 등록할 수 있습니다");
        }

        // URL 형식 검증 (간단한 패턴)
        Pattern urlPattern = Pattern.compile(
                "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$"
        );

        for (String url : imageUrl) {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("빈 이미지 URL은 등록할 수 없습니다");
            }
            if (!urlPattern.matcher(url).matches()) {
                throw new IllegalArgumentException("올바른 이미지 URL 형식이 아닙니다: " + url);
            }
        }
    }

    /**
     * 강좌 내용 검증
     * - 필수 값
     * - 최소 10자
     * - 최대 500자
     */
    private static void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용은 필수입니다");
        }
        if (content.length() < 10) {
            throw new IllegalArgumentException("내용은 최소 10자 이상이어야 합니다");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("내용은 500자를 초과할 수 없습니다");
        }
    }

    /**
     * 만남 장소 검증
     * - 필수 값
     * - 최대 200자
     */
    private static void validateMeetingPlace(String meetingPlace) {
        if (meetingPlace == null || meetingPlace.trim().isEmpty()) {
            throw new IllegalArgumentException("만남 장소는 필수입니다");
        }
        if (meetingPlace.length() > 200) {
            throw new IllegalArgumentException("만남 장소는 200자를 초과할 수 없습니다");
        }
    }

    /**
     * 연락처 검증
     * - 필수 값
     * - 전화번호 형식 (010-0000-0000 또는 01000000000)
     */
    private static void validateContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("연락처는 필수입니다");
        }

        // 전화번호 형식 검증
        Pattern phonePattern = Pattern.compile("^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$");
        if (!phonePattern.matcher(contact).matches()) {
            throw new IllegalArgumentException("올바른 전화번호 형식이 아닙니다 (예: 010-1234-5678)");
        }
    }

    /**
     * 비용 검증
     * - 선택 (무료 강좌 가능)
     * - 값이 있으면 "무료" 또는 숫자
     * - 숫자인 경우 0 이상
     */
    private static void validateCost(String cost) {
        if (cost == null || cost.trim().isEmpty()) {
            return; // null 허용 (무료로 간주)
        }

        // "무료"인 경우 허용
        if (cost.trim().equals("무료")) {
            return;
        }

        // 숫자인 경우 검증
        try {
            int costValue = Integer.parseInt(cost.replaceAll("[^0-9]", ""));
            if (costValue < 0) {
                throw new IllegalArgumentException("비용은 0원 이상이어야 합니다");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("비용은 '무료' 또는 숫자여야 합니다");
        }
    }

    /**
     * 최대 인원 검증
     * - 필수 값
     * - 1명 이상
     * - 100명 이하
     */
    private static void validateMaxNum(Integer maxNum) {
        if (maxNum == null) {
            throw new IllegalArgumentException("최대 인원은 필수입니다");
        }
        if (maxNum < 1) {
            throw new IllegalArgumentException("최대 인원은 1명 이상이어야 합니다");
        }
        if (maxNum > 100) {
            throw new IllegalArgumentException("최대 인원은 100명을 초과할 수 없습니다");
        }
    }

    /**
     * 나이 제한 검증
     * - 필수 값
     * - "제한없음" 또는 "만 X세 이상" 형식
     */
    private static void validateAgeLimit(String ageLimit) {
        if (ageLimit == null || ageLimit.trim().isEmpty()) {
            throw new IllegalArgumentException("나이 제한 정보는 필수입니다");
        }

        // "제한없음"인 경우 허용
        if (ageLimit.trim().equals("제한없음")) {
            return;
        }

        // "만 X세 이상" 형식 검증
        Pattern agePattern = Pattern.compile("^만 [0-9]{1,2}세 이상$");
        if (!agePattern.matcher(ageLimit).matches()) {
            throw new IllegalArgumentException("나이 제한은 '제한없음' 또는 '만 X세 이상' 형식이어야 합니다");
        }
    }

    /**
     * 취소 정책 검증
     * - 선택
     * - 값이 있으면 최대 200자
     */
    private static void validateCancelPolicy(String cancelPolicy) {
        if (cancelPolicy != null && cancelPolicy.length() > 200) {
            throw new IllegalArgumentException("취소 정책은 200자를 초과할 수 없습니다");
        }
    }

    /**
     * 날씨 정책 검증
     * - 선택
     * - 값이 있으면 최대 200자
     */
    private static void validateWeatherPolicy(String weatherPolicy) {
        if (weatherPolicy != null && weatherPolicy.length() > 200) {
            throw new IllegalArgumentException("날씨 정책은 200자를 초과할 수 없습니다");
        }
    }

    private static void validateHighlights(List<String> highlights) {
        if (highlights == null) {
            return;
        }
        if (highlights.size() > 10) {
            throw new IllegalArgumentException("하이라이트는 최대 10개까지 등록할 수 있습니다");
        }
        for (String highlight : highlights) {
            if (highlight == null || highlight.trim().isEmpty()) {
                throw new IllegalArgumentException("빈 하이라이트는 등록할 수 없습니다");
            }
            if (highlight.length() > 100) {
                throw new IllegalArgumentException("각 하이라이트는 100자를 초과할 수 없습니다");
            }
        }
    }

    private static void validateExclusions(List<String> exclusions) {
        if (exclusions == null) {
            return;
        }
        if (exclusions.size() > 10) {
            throw new IllegalArgumentException("불포함 사항은 최대 10개까지 등록할 수 있습니다");
        }
        for (String exclusion : exclusions) {
            if (exclusion == null || exclusion.trim().isEmpty()) {
                throw new IllegalArgumentException("빈 불포함 사항은 등록할 수 없습니다");
            }
            if (exclusion.length() > 100) {
                throw new IllegalArgumentException("각 불포함 사항은 100자를 초과할 수 없습니다");
            }
        }
    }

    private static void validateInclusions(List<String> inclusions) {
        if (inclusions == null) {
            return;
        }
        if (inclusions.size() > 10) {
            throw new IllegalArgumentException("포함 사항은 최대 10개까지 등록할 수 있습니다");
        }
        for (String inclusion : inclusions) {
            if (inclusion == null || inclusion.trim().isEmpty()) {
                throw new IllegalArgumentException("빈 포함 사항은 등록할 수 없습니다");
            }
            if (inclusion.length() > 100) {
                throw new IllegalArgumentException("각 포함 사항은 100자를 초과할 수 없습니다");
            }
        }
    }
}
