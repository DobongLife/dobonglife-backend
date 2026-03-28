package com.umust.dobonglife.global.common.response.slice;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class SliceResponse<T> {
    private final List<T> content;
    private final int size;
    private final boolean hasNext;
    private final String nextCursor;
}