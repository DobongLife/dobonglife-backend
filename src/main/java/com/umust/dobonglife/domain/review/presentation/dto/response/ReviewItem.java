package com.umust.dobonglife.domain.review.presentation.dto.response;

import java.time.LocalDate;

public record ReviewItem(Long reviewId,
                         String placeName,
                         String courseName,
                         double rating,
                         String contentSummary,
                         LocalDate writtenDate,
                         int likeCount,
                         int imageCount,
                         String thumbnailUrl) {
}
