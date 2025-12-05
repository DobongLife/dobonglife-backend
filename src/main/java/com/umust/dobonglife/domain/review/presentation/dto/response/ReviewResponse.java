package com.umust.dobonglife.domain.review.presentation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReviewResponse(Long reviewId,
                             String title,
                             String content) {
}
