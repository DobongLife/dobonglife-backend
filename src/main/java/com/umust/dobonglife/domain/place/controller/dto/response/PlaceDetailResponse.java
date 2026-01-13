package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceDetailResponse {

    private Long placeId;
    private String name;
    private String subName;
    private String content;
    private List<String> amenities;
    private String address;
    private List<String> placeImages;
    private String operatingHour;
    private String contact;
    private Double averageRating;
    private Long reviewCount;
    private double latitude;
    private double longitude;
    private CursorResponse<ReviewSummaryResponse> reviews;

    public static PlaceDetailResponse from(Place place, CursorResponse<ReviewSummaryResponse> reviews) {
        return PlaceDetailResponse.builder()
                .placeId(place.getId())
                .name(place.getName())
                .subName(place.getSubName())
                .content(place.getContent())
                .amenities(Place.amenityToStrings(place))
                .address(place.getAddress())
                .contact(place.getContact())
                .averageRating(place.getAverageRating())
                .reviewCount(place.getReviewCount())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .operatingHour(place.getOperatingHour())
                .reviews(reviews)
                .build();
    }
}
