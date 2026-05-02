package com.umust.dobonglife.domain.review.infrastructure.jpa.custom;

import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;

import java.util.List;

public interface ReviewRepositoryCustom {

    List<ReviewSummaryResponse> findReviews(TargetType targetType, Long targetId, Long lastId, int size);

    List<MyReviewResponse> findMyReviews(Long userId, TargetType targetType, Long lastId, int size);
}
