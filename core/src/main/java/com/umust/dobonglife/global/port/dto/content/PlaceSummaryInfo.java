package com.umust.dobonglife.global.port.dto.content;

import com.umust.dobonglife.global.common.Identifiable;

import java.util.List;

public record PlaceSummaryInfo(
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
        String status
) implements Identifiable {
    @Override
    public Long getId() { return placeId; }

    public PlaceSummaryInfo withLiked(boolean isLiked) {
        return new PlaceSummaryInfo(placeId, placeName, category, thumbnailUrl,
                averageRating, reviewCount, isLiked, latitude, longitude, themes, status);
    }
}
