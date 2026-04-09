package com.umust.dobonglife.presentation.business.dto.response;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceDetail;
import com.umust.dobonglife.domain.place.domain.entity.PlaceImage;
import com.umust.dobonglife.domain.place.domain.entity.PlaceTheme;
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

    public static BusinessResponse of(Business business, Place place) {
        PlaceDetail detail = place.getDetail();

        return BusinessResponse.builder()
                .businessId(business.getId())
                .businessNumber(business.getBusinessNumber())
                .email(business.getEmail())
                .managerName(business.getManagerName())
                .userId(business.getUserId())
                .placeId(place.getId())
                .placeName(place.getName())
                .subName(detail != null ? detail.getSubName() : null)
                .content(detail != null ? detail.getContent() : null)
                .address(detail != null ? detail.getAddress() : null)
                .contact(detail != null ? detail.getContact() : null)
                .operatingHour(detail != null ? detail.getOperatingHour() : null)
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .thumbnailUrl(place.getThumbnailUrl())
                .imageUrls(place.getImages().stream()
                        .map(PlaceImage::getImageUrl)
                        .toList())
                .category(place.getCategory() != null ? place.getCategory().getDescription() : null)
                .themes(place.getThemes().stream()
                        .map(t -> t.getTheme().name())
                        .toList())
                .build();
    }
}
