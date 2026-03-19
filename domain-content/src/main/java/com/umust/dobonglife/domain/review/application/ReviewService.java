package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public CursorResponse<ReviewSummaryResponse> getPlaceReviews(Long placeId, Long lastId, int size) {
        Slice<ReviewSummaryResponse> slice = reviewRepository.findReviewsByPlaceId(placeId, lastId, size);
        return CursorUtils.toCursorResponse(slice, r -> r);
    }
}
