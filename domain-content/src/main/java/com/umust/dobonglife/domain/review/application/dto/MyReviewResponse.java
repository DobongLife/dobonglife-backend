package com.umust.dobonglife.domain.review.application.dto;

import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

public record MyReviewResponse(
        Long reviewId,
        Long targetId,
        String targetName,
        String targetThumbnailUrl,
        Double rating,
        String content,
        String thumbnailUrl,
        List<String> imageUrls,
        LocalDateTime updatedAt
) implements Identifiable {

    @Override
    public Long getId() {
        return reviewId;
    }
}
