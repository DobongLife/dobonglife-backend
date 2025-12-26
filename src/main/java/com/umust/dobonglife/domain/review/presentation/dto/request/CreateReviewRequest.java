package com.umust.dobonglife.domain.review.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateReviewRequest(Long courseId,
                                  Long placeId,
                                  @NotNull(message = "리뷰평점은 필수입니다")
                                  Double rating,
                                  @NotNull(message = "리뷰제목은 필수입니다")
                                  String title,
                                  @NotNull(message = "리뷰내용은 필수입니다")
                                  String content,
                                  List<String> imageUrls) {
}
