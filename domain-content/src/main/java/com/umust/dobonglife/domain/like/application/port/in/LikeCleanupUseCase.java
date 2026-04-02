package com.umust.dobonglife.domain.like.application.port.in;

public interface LikeCleanupUseCase {

    void markPendingByUserId(Long userId);

    void finalizeByUserId(Long userId);
}
