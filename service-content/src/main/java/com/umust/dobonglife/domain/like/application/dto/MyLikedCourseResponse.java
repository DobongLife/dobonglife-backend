package com.umust.dobonglife.domain.like.application.dto;

import com.umust.dobonglife.global.common.Identifiable;
import com.umust.dobonglife.global.common.model.BaseStatus;

import java.util.List;

public record MyLikedCourseResponse(
        Long courseId,
        String title,
        String subTitle,
        String level,
        Long duration,
        String thumbnailUrl,
        Double averageRating,
        Long reviewCount,
        boolean isLiked,
        List<String> themes,
        BaseStatus status
) implements Identifiable {

    @Override
    public Long getId() {
        return courseId;
    }
}
