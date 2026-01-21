package com.umust.dobonglife.domain.course.controller.dto;

import com.umust.dobonglife.domain.course.domain.entity.Course;

/**
 * 리뷰 요약 정보
 * 여러 DTO에서 재사용
 */
public record ReviewSummary(
        Double rating,
        Long count
) {
    public static ReviewSummary from(Course course) {
        Double averageRating = course.getReviewStats().getAverageRating();

        Double roundedRating = (averageRating != null)
                ? Math.round(averageRating * 100.0) / 100.0
                : 0.0;

        return new ReviewSummary(
                roundedRating,
                course.getReviewStats().getReviewCount()
        );
    }
}
