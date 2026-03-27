package com.umust.dobonglife.domain.point.application.port.in;

public interface PointCleanupUseCase {

    void deleteByUserId(Long userId);
}
