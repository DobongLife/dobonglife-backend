package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.place.domain.entity.Place;
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
    private boolean isLiked;

    public static PlaceSummaryResponse from(Place place) {
        return PlaceSummaryResponse.builder()
                .placeId(place.getId())
                .placeName(place.getName())
                .thumbnailUrl(place.getThumbnailUrl())
                .averageRating(place.getAverageRating())
                .reviewCount(place.getReviewCount())
                .build();
    }
}
