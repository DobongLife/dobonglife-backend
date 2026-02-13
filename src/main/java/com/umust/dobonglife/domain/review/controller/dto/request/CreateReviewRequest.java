package com.umust.dobonglife.domain.review.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(Long courseId,
                                  Long placeId,
                                  Double oldRating,
                                  @NotNull(message = "리뷰평점은 필수입니다")
                                  Double newRating,
                                  @NotNull(message = "리뷰내용은 필수입니다")
                                  String content) {
}
