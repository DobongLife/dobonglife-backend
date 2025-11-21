package com.umust.dobonglife.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceResponseList {
    List<PlaceResponse> responseList;

    public static PlaceResponseList of(final List<PlaceResponse> responseList) {
        return builder().responseList(responseList).build();
    }
}
