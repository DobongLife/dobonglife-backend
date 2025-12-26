package com.umust.dobonglife.domain.courseLike.controller.dto.request;

public record CourseLikeResponse(Long userId,
                                 Long courseId,
                                 boolean isFavorite) {
    public static CourseLikeResponse from(Long userId, Long courseId, boolean isFavorite) {
        return new CourseLikeResponse(
                userId,
                courseId,
                isFavorite
        );
    }
}
