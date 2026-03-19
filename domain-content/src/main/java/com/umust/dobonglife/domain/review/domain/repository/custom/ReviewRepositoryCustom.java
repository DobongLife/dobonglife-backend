package com.umust.dobonglife.domain.review.domain.repository.custom;

import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    Slice<ReviewSummaryResponse> findReviews(TargetType targetType, Long targetId, Long lastId, int size);
}
