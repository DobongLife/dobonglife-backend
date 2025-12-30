package com.umust.dobonglife.domain.review.controller.dto.response;
import com.umust.dobonglife.domain.review.domain.entity.Review;

public record ReviewResponse(Long reviewId,
                             String content) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent()
        );
    }
}
