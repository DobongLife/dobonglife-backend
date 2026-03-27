package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
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
public class ReviewService implements ReviewCleanupUseCase, ReviewRestoreUseCase {

    private final ReviewRepository reviewRepository;

    public CursorResponse<ReviewSummaryResponse> getPlaceReviews(Long placeId, Long lastId, int size) {
        Slice<ReviewSummaryResponse> slice = reviewRepository.findReviewsByPlaceId(placeId, lastId, size);
        return CursorUtils.toCursorResponse(slice, r -> r);
    }

    public CursorResponse<ReviewSummaryResponse> getCourseReviews(Long courseId, Long lastId, int size) {
        Slice<ReviewSummaryResponse> slice = reviewRepository.findReviewsByCourseId(courseId, lastId, size);
        return CursorUtils.toCursorResponse(slice, r -> r);
    }

    @Override
    @Transactional
    public void nullifyByUserId(Long userId) {
        reviewRepository.nullifyUserByUserId(userId);
    }

    @Override
    @Transactional
    public void restoreByUserId(Long userId) {
        // 리뷰 userId nullify는 역연산 불가 → 트랜잭션 롤백으로 복구
    }
}
