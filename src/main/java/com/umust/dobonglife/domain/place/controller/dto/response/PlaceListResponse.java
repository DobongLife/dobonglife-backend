package com.umust.dobonglife.domain.place.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceListResponse {
    List<PlaceResponse> responseList;

    public static PlaceListResponse from(final List<PlaceResponse> responseList) {
        return builder().responseList(responseList).build();
    }
}
