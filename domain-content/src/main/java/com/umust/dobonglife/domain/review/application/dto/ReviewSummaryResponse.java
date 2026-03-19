package com.umust.dobonglife.domain.review.application.dto;

import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;

public record ReviewSummaryResponse(
        Long reviewId,
        Long userId,
        Double rating,
        String content,
        String thumbnailUrl,
        LocalDateTime createdAt
) implements Identifiable {

    @Override
    public Long getId() {
        return reviewId;
    }
}
