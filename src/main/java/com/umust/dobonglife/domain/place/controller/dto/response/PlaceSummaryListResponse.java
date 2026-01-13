package com.umust.dobonglife.domain.place.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceSummaryListResponse {
    List<PlaceSummaryResponse> responseList;

    public static PlaceSummaryListResponse from(final List<PlaceSummaryResponse> responseList) {
        return builder().responseList(responseList).build();
    }
}