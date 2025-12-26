package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.point.infrastructure.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    public boolean processUserPoint(Long userId) {
        Long currentPoint = getUserPoint(userId);
        return isValid(currentPoint);
    }

    public Long getUserPoint(Long userId) {
        Long amount = pointRepository.sumAmountByUserId(userId);
        return (amount != null) ? amount : 0L;
    }

    private boolean isValid(Long point) {
        return point != null && point >= 0;
    }
}
