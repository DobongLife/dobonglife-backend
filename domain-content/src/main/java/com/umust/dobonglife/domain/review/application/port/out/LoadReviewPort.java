package com.umust.dobonglife.domain.review.application.port.out;

import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.global.common.constant.TargetType;

import java.util.List;
import java.util.Optional;

public interface LoadReviewPort {

    Optional<Review> findById(Long reviewId);

    List<ReviewSummaryResponse> findReviews(TargetType targetType, Long targetId, Long lastId, int size);

    List<MyReviewResponse> findMyReviews(Long userId, TargetType targetType, Long lastId, int size);
}
