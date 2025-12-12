package com.umust.dobonglife.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SliceResponse<T> {
    private final List<T> content;
    private final SortResponse sort;   // 유지
    private final int size;            // 요청 size 그대로(선택)
    private final boolean hasNext;     // 다음 페이지 존재 여부
    private final String nextCursor;
}