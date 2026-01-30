package com.umust.dobonglife.domain.business.controller.dto.response;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.constant.PlaceCategory;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessResponse {

    // ===== Business 정보 =====
    private Long businessId;
    private String businessNumber;
    private String email;
    private String managerName;
    private Long userId;

    // ===== Place 기본 정보 =====
    private Long placeId;
    private String placeName;
    private String subName;
    private String content;
    private String address;
    private String contact;
    private String operatingHour;

    // ===== Place 위치/이미지 =====
    private Double latitude;
    private Double longitude;
    private String thumbnailUrl;
    private List<String> imageUrls;

    // ===== Place 세부사항 =====
    private PlaceCategory category;
    private List<Amenity> amenities;
    private List<CourseTheme> themes;

    public static BusinessResponse from(Business business) {
        Place place = business.getPlace();

        return BusinessResponse.builder()
                // Business
                .businessId(business.getId())
                .businessNumber(business.getBusinessNumber())
                .email(business.getEmail())
                .managerName(business.getManagerName())
                .userId(business.getUser().getId())

                // Place
                .placeId(place.getId())
                .placeName(place.getName())
                .subName(place.getSubName())
                .content(place.getContent())
                .address(place.getAddress())
                .contact(place.getContact())
                .operatingHour(place.getOperatingHour())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .thumbnailUrl(place.getThumbnailUrl())
                .imageUrls(place.getImageUrls())
                .category(place.getCategory())
                .amenities(place.getAmenities())
                .themes(place.getThemes())
                .build();
    }
}
