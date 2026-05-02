package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
import com.umust.dobonglife.domain.review.application.port.out.SaveReviewPort;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService implements ReviewCleanupUseCase, ReviewRestoreUseCase {

    private final SaveReviewPort saveReviewPort;

    @Override
    @Transactional
    public void markPendingByUserId(Long userId) {
        saveReviewPort.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void finalizeByUserId(Long userId) {
        // PENDING 리뷰의 userId를 null로 설정하고 ACTIVE로 복원 (익명 리뷰로 유지)
        saveReviewPort.nullifyUserAndUpdateStatus(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }

    @Override
    @Transactional
    public void restoreByUserId(Long userId) {
        saveReviewPort.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }
}
