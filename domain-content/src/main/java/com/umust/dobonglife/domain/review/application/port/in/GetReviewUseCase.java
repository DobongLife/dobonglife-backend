package com.umust.dobonglife.domain.review.application.port.in;

import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;

public interface GetReviewUseCase {

    CursorResponse<ReviewSummaryResponse> getReviews(TargetType targetType, Long targetId, Long lastId, int size);

    CursorResponse<MyReviewResponse> getMyReviews(Long userId, TargetType targetType, Long lastId, int size);
}
