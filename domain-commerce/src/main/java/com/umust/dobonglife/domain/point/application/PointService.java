package com.umust.dobonglife.domain.point.application;

import com.umust.dobonglife.domain.point.application.port.in.PointCleanupUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointRestoreUseCase;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.domain.point.domain.repository.PointHistoryRepository;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.point.exception.PointErrorCode;
import com.umust.dobonglife.domain.point.exception.PointException;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService implements PointCleanupUseCase, PointRestoreUseCase {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void deduct(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        point.deduct(amount);

        pointHistoryRepository.save(
                PointHistory.ofDeduction(point.getId(), amount, point.getBalance(), "쿠폰 교환"));
    }

    @Transactional
    public void refund(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        point.refund(amount);

        pointHistoryRepository.save(
                PointHistory.ofRefund(point.getId(), amount, point.getBalance(), "쿠폰 교환 취소"));
    }

    @Override
    @Transactional
    public void markPendingByUserId(Long userId) {
        pointHistoryRepository.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
        pointRepository.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void finalizeByUserId(Long userId) {
        pointHistoryRepository.deleteByUserIdAndStatus(userId, BaseStatus.PENDING);
        pointRepository.deleteByUserIdAndStatus(userId, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void restoreByUserId(Long userId) {
        pointRepository.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
        pointHistoryRepository.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }
}
