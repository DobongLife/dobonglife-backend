package com.umust.dobonglife.presentation.business.dto.response;

import com.umust.dobonglife.application.business.service.BusinessFacade;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BusinessResponse {

    private Long businessId;
    private String businessNumber;
    private String email;
    private String managerName;
    private Long userId;

    private Long placeId;
    private String placeName;
    private String subName;
    private String content;
    private String address;
    private String contact;
    private String operatingHour;

    private Double latitude;
    private Double longitude;
    private String thumbnailUrl;
    private List<String> imageUrls;

    private String category;
    private List<String> themes;

    public static BusinessResponse of(BusinessFacade.BusinessInfo info) {
        return BusinessResponse.builder()
                .businessId(info.businessId())
                .businessNumber(info.businessNumber())
                .email(info.email())
                .managerName(info.managerName())
                .userId(info.userId())
                .placeId(info.placeId())
                .placeName(info.placeName())
                .subName(info.subName())
                .content(info.content())
                .address(info.address())
                .contact(info.contact())
                .operatingHour(info.operatingHour())
                .latitude(info.latitude())
                .longitude(info.longitude())
                .thumbnailUrl(info.thumbnailUrl())
                .imageUrls(info.imageUrls())
                .category(info.category())
                .themes(info.themes())
                .build();
    }
}
