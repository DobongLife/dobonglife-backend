package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceDetailResponse {

    private Long placeId;
    private String name;
    private String content;
    private List<String> amenities;
    private String address;
    private List<String> placeImages;
    private String operatingHour;
    private String contact;
    private Double averageRating;
    private Long reviewCount;

    public static PlaceDetailResponse from(Place place) {
        return PlaceDetailResponse.builder()
                .placeId(place.getId())
                .name(place.getName())
                .content(place.getContent())
                .address(place.getAddress())
                .contact(place.getContact())
                .averageRating(place.getAverageRating())
                .reviewCount(place.getReviewCount())
                .build();
    }
}
