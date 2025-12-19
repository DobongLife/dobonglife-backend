package com.umust.dobonglife.domain.reviewLike.controller.dto.request;

public record ReviewLikeResponse(Long userId,
                                 Long courseId,
                                 Long placeId,
                                 boolean isFavorite) {
    public static ReviewLikeResponse from(Long userId, Long courseId, Long placeId, boolean isFavorite) {
        return new ReviewLikeResponse(
                userId,
                courseId,
                placeId,
                isFavorite
        );
    }
}
