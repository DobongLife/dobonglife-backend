package com.umust.dobonglife.domain.review.application.port.in;

public interface ReviewCleanupUseCase {

    void markPendingByUserId(Long userId);

    void finalizeByUserId(Long userId);
}
