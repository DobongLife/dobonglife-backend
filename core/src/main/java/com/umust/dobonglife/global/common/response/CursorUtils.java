package com.umust.dobonglife.global.common.response;

import com.umust.dobonglife.global.common.Identifiable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;

public class CursorUtils {
    public static <E, R extends Identifiable> CursorResponse<R> toCursorResponse(
            Slice<E> slice,
            Function<E, R> mapper) {  // idExtractor 제거

        List<R> content = slice.getContent().stream()
                .map(mapper)
                .toList();

        Long lastId = content.isEmpty()
                ? null
                : content.get(content.size() - 1).getId();

        return new CursorResponse<>(content, lastId, slice.hasNext());
    }
}