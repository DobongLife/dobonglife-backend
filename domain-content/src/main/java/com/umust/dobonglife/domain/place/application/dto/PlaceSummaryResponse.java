package com.umust.dobonglife.domain.place.application.dto;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceTheme;
import com.umust.dobonglife.global.common.model.BaseStatus;

import java.util.List;

public record PlaceSummaryResponse(
        Long placeId,
        String placeName,
        String category,
        String thumbnailUrl,
        Double averageRating,
        Long reviewCount,
        boolean isLiked,
        double latitude,
        double longitude,
        List<String> themes,
        BaseStatus status
) {

    public static PlaceSummaryResponse from(Place place) {
        return new PlaceSummaryResponse(
                place.getId(),
                place.getName(),
                place.getCategory().name(),
                place.getThumbnail() != null ? place.getThumbnail().getImageUrl() : null,
                place.getAverageRating(),
                place.getReviewCount(),
                false,
                place.getLatitude(),
                place.getLongitude(),
                place.getThemes().stream().map(t -> t.getTheme().name()).toList(),
                place.getStatus()
        );
    }

    public PlaceSummaryResponse withLiked(boolean isLiked) {
        return new PlaceSummaryResponse(
                placeId, placeName, category, thumbnailUrl,
                averageRating, reviewCount, isLiked,
                latitude, longitude, themes, status
        );
    }
}
