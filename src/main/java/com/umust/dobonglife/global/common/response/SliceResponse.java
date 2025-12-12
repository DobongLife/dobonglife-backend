package com.umust.dobonglife.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SliceResponse<T> {
    private final List<T> content;
    private final SortResponse sort;
    private final int size;
    private final boolean hasNext;
    private final String nextCursor;
}