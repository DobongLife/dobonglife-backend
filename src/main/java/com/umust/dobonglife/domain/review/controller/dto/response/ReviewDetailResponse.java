package com.umust.dobonglife.domain.review.controller.dto.response;

import com.umust.dobonglife.domain.review.domain.entity.Review;

import java.util.List;

public record ReviewDetailResponse(Double rating,
                                   String content,
                                   List<String> imageUrls) {
    public static ReviewDetailResponse from(Review review) {
        return new ReviewDetailResponse(
                review.getRating(),
                review.getContent(),
                review.getImageUrls()
        );
    }
}
