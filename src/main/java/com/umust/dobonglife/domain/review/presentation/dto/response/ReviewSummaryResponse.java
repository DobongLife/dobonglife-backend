package com.umust.dobonglife.domain.review.presentation.dto.response;

import com.umust.dobonglife.domain.review.domain.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewSummaryResponse(String name,
                                    Double rating,
                                    String content,
                                    List<String> imageUrls,
                                    LocalDateTime updatedAt,
                                    boolean owner
                                    ) {
    public static ReviewSummaryResponse from(Long userId, Review review) {
        boolean owner = userId == review.getId(); // TODO: 위치 다시 고민
        return new ReviewSummaryResponse(
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getUpdatedAt(),
                owner
        );
    }
}
