package com.umust.dobonglife.domain.reviewLike.controller.dto.response;

public record ReviewLikeResponse(Long userId,
                                 Long reviewId,
                                 boolean isFavorite) {
    public static ReviewLikeResponse from(Long userId, Long reviewId,boolean isFavorite) {
        return new ReviewLikeResponse(
                userId,
                reviewId,
                isFavorite
        );
    }
}
