package com.umust.dobonglife.domain.review.application.port.in;

public interface ReviewCleanupUseCase {

    void nullifyByUserId(Long userId);
}
