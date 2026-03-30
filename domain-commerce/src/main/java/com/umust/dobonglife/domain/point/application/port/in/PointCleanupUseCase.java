package com.umust.dobonglife.domain.point.application.port.in;

public interface PointCleanupUseCase {

    void markPendingByUserId(Long userId);

    void finalizeByUserId(Long userId);
}
