package com.umust.dobonglife.domain.place.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
@AllArgsConstructor
public class PlaceSummaryResponse {
    private Long placeId;
    private String placeName;
    private String thumbnailUrl;
    private Double averageRating;
    private Long reviewCount;
}
