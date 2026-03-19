package com.umust.dobonglife.domain.point.application;

import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.domain.point.domain.repository.PointHistoryRepository;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.point.exception.PointErrorCode;
import com.umust.dobonglife.domain.point.exception.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

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
}
