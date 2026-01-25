package com.umust.dobonglife.domain.review.controller.dto.response;
import com.umust.dobonglife.domain.review.domain.entity.Review;

import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.REVIEW_CREATE;

public record ReviewResponse(Long reviewId,
                             String content,
                             Long point) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                REVIEW_CREATE.getPoint()
        );
    }
}
