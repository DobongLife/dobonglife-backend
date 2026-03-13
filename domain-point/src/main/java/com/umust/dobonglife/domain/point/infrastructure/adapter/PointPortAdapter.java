package com.umust.dobonglife.domain.point.infrastructure.adapter;

import com.umust.dobonglife.domain.point.application.PointService;
import com.umust.dobonglife.global.port.PointPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPortAdapter implements PointPort {

    private final PointService pointService;

    @Override
    public void deduct(Long sagaId, Long userId, Long amount) {
        pointService.deduct(userId, amount);
    }

    @Override
    public void refund(Long sagaId, Long userId, Long amount) {
        pointService.refund(userId, amount);
    }
}
