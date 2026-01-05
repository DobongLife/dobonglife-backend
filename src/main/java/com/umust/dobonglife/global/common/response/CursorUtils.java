package com.umust.dobonglife.global.common.response;

import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;

public class CursorUtils {

    public static <E, R> CursorResponse<R> toCursorResponse(Slice<E> slice, Function<E, R> mapper) {
        List<R> content = slice.getContent().stream()
                .map(mapper)
                .toList();

        return new CursorResponse<>(content, slice.hasNext());
    }
}
