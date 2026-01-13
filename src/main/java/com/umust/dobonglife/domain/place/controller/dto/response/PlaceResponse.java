package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlaceResponse {
    private String placeName;

    private String content;

    private List<String> amenities;

    private String address;

    private String contact;

    private String operatingHour;

    private Double averageRating;

    private Long reviewCount;

    public static PlaceResponse from(Place place) {
        return PlaceResponse.builder()
                .placeName(place.getName())
                .content(place.getContent())
                .address(place.getAddress())
                .contact(place.getContact())
                .operatingHour(place.getOperatingHour())
                .amenities(place.getAmenities()
                        .stream()
                        .map(Amenity::toValue)
                        .toList())
                .averageRating(place.getAverageRating())
                .reviewCount(place.getReviewCount())
                .build();
    }
}
