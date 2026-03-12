package com.umust.dobonglife.domain.review.domain.repository.custom;

import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    Slice<ReviewSummaryResponse> findReviewsByPlaceId(Long placeId, Long lastId, int size);
}
