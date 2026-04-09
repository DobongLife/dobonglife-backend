package com.umust.dobonglife.domain.course.application.dto;

import com.umust.dobonglife.global.common.Identifiable;

public record CourseSummaryResponse(
        Long courseId,
        String title,
        String subTitle,
        String level,
        Long duration,
        String thumbnailUrl,
        Double averageRating,
        Long reviewCount,
        boolean isLiked
) implements Identifiable {

    @Override
    public Long getId() {
        return courseId;
    }

    public CourseSummaryResponse withLiked(boolean isLiked) {
        return new CourseSummaryResponse(
                courseId, title, subTitle, level, duration,
                thumbnailUrl, averageRating, reviewCount, isLiked
        );
    }
}
