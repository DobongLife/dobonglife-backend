package com.umust.dobonglife.domain.like.application.dto;

import com.umust.dobonglife.global.common.Identifiable;
import com.umust.dobonglife.global.common.model.BaseStatus;

import java.util.List;

public record MyLikedPlaceResponse(
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
) implements Identifiable {

    @Override
    public Long getId() {
        return placeId;
    }
}
