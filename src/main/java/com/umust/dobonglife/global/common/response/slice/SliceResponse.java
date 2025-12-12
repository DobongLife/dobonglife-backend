package com.umust.dobonglife.global.common.response.slice;

import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
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

    public SliceResponse(List<PointResponse> responses, SortResponse desc, boolean hasNext, String nextCursor) {
    }
}