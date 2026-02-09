package com.umust.dobonglife.domain.review.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewSummaryResponse(Long reviewId,
                                          String name,
                                          Double rating,
                                          String content,
                                          List<String> imageUrls,
                                          LocalDateTime updatedAt,
                                          boolean owner
) implements Identifiable {
    public static ReviewSummaryResponse from(Long userId, Review review) {
        boolean owner = userId == review.getUser().getId(); // TODO: 위치 다시 고민
        return new ReviewSummaryResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getUpdatedAt(),
                owner
        );
    }

    @JsonIgnore
    @Override
    public Long getId() {
        return reviewId;
    }
}

