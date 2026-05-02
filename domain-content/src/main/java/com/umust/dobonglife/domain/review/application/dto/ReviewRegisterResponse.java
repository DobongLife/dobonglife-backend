package com.umust.dobonglife.domain.review.application.dto;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.entity.ReviewImage;

import java.util.List;

public record ReviewRegisterResponse(
        Long reviewId,
        String content,
        Double rating,
        String thumbnailUrl,
        List<String> imageUrls
) {
    public static ReviewRegisterResponse from(Review review) {
        return new ReviewRegisterResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getThumbnailUrl(),
                review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList()
        );
    }
}
