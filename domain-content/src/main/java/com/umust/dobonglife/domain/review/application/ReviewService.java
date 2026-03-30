package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.global.common.model.BaseStatus;
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
    public void markPendingByUserId(Long userId) {
        reviewRepository.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void finalizeByUserId(Long userId) {
        // PENDING 리뷰의 userId를 null로 설정하고 ACTIVE로 복원 (익명 리뷰로 유지)
        reviewRepository.nullifyUserAndUpdateStatus(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void restoreByUserId(Long userId) {
        reviewRepository.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }
}
