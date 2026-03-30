package com.umust.dobonglife.domain.coupon.application.port.in;

public interface CouponCleanupUseCase {

    void markPendingByUserId(Long userId);

    void finalizeByUserId(Long userId);
}
