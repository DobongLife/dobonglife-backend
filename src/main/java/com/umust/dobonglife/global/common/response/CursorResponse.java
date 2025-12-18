package com.umust.dobonglife.global.common.response;

import lombok.Getter;

import java.util.List;

@Getter
public class CursorResponse<T> {
    private List<T> content;
    private Long lastId;
    private boolean hasNext;

    public CursorResponse(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
        this.lastId = content.isEmpty() ? null : extractId(content.get(content.size() - 1));
    }

    private Long extractId(T lastItem) {
        // 응답 객체에서 ID를 뽑아내는 로직 (예: reflection이나 인터페이스 활용)
        // 여기서는 간단하게 CourseSummaryResponse라고 가정하거나
        // Service에서 직접 계산해서 넘겨주는 방식을 추천합니다.
        return null;
    }
}
