package com.umust.dobonglife.domain.review.application.port.in;

import com.umust.dobonglife.domain.review.application.dto.CreateReviewRequest;
import com.umust.dobonglife.domain.review.application.dto.ReviewRegisterResponse;
import com.umust.dobonglife.domain.review.application.dto.UpdateReviewRequest;

public interface ManageReviewUseCase {

    ReviewRegisterResponse createReview(Long userId, CreateReviewRequest request);

    ReviewRegisterResponse updateReview(Long reviewId, Long userId, UpdateReviewRequest request);

    void deleteReview(Long reviewId, Long userId);
}
