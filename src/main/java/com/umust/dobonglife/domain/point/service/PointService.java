package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.point.infrastructure.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    public Long getUserPoint(Long userId) {
        return pointRepository.sumAmountByUserId(userId);
    }
}
