package com.umust.dobonglife.global.common.response;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Slf4j
public class CursorResponse<T> {
    private List<T> content;
    private Long lastId;
    private boolean hasNext;

    public CursorResponse(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
        this.lastId = content.isEmpty() ? null : extractId(content.get(content.size() - 1));
    }

    private Long extractId(T lastItem) { // TODO: 인터페이스로 처리 고려
        if (lastItem == null) return null;

        try {
            java.lang.reflect.Field field = lastItem.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return (Long) field.get(lastItem);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("ID 추출 실패: {}", e.getMessage());
            return null;
        }
    }
}
