package com.umust.dobonglife.domain.place.controller.dto.response;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.global.common.Identifiable;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PlaceSummaryResponse implements Identifiable {
    private Long placeId;
    private String placeName;
    private String category;
    private String thumbnailUrl;
    private Double averageRating;
    private Long reviewCount;
    private boolean isLiked;
    private double latitude;
    private double longitude;
    private List<String> themes;
    private BaseStatus status;

    public static PlaceSummaryResponse from(Place place, boolean isLiked, List<CourseTheme> themes) {
        return PlaceSummaryResponse.builder()
                .placeId(place.getId())
                .placeName(place.getName())
                .category(place.getCategory().getDescription())
                .thumbnailUrl(place.getThumbnailUrl())
                .averageRating(place.getAverageRating())
                .reviewCount(place.getReviewCount())
                .isLiked(isLiked)
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .themes(
                        themes.stream()
                                .map(CourseTheme::name)
                                .toList())
                .status(place.getStatus())
                .build();
    }

    @Override
    public Long getId() {
        return placeId;
    }
}
