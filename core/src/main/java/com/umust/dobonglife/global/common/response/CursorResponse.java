package com.umust.dobonglife.global.common.response;

import com.umust.dobonglife.global.common.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Slf4j
@AllArgsConstructor
public class CursorResponse<T extends Identifiable> {
    private List<T> content;
    private Long lastId;
    private boolean hasNext;

    public CursorResponse(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
        this.lastId = content.isEmpty() ? null : content.get(content.size() - 1).getId();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Identifiable> CursorResponse<T> empty() {
        return new CursorResponse<>(List.of(), null, false);
    }
}
