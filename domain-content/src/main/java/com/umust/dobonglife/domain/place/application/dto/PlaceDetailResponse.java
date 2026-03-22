package com.umust.dobonglife.domain.place.application.dto;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceDetail;
import com.umust.dobonglife.domain.place.domain.entity.PlaceImage;
import com.umust.dobonglife.domain.place.domain.entity.PlaceTheme;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;

public record PlaceDetailResponse(
        Long placeId,
        String name,
        String subName,
        String category,
        String content,
        String address,
        List<String> placeImages,
        String operatingHour,
        String contact,
        List<String> themes,
        Double averageRating,
        Long reviewCount,
        double latitude,
        double longitude,
        boolean isLiked,
        CursorResponse<ReviewSummaryResponse> reviews
) {

    public static PlaceDetailResponse of(Place place, boolean isLiked, CursorResponse<ReviewSummaryResponse> reviews) {
        PlaceDetail detail = place.getDetail();

        return new PlaceDetailResponse(
                place.getId(),
                place.getName(),
                detail != null ? detail.getSubName() : null,
                place.getCategory().name(),
                detail != null ? detail.getContent() : null,
                detail != null ? detail.getAddress() : null,
                place.getImages().stream().map(PlaceImage::getImageUrl).toList(),
                detail != null ? detail.getOperatingHour() : null,
                detail != null ? detail.getContact() : null,
                place.getThemes().stream().map(t -> t.getTheme().name()).toList(),
                place.getAverageRating(),
                place.getReviewCount(),
                place.getLatitude(),
                place.getLongitude(),
                isLiked,
                reviews
        );
    }
}
