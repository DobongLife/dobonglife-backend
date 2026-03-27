package com.umust.dobonglife.domain.coupon.application.port.in;

public interface CouponCleanupUseCase {

    void deleteByUserId(Long userId);
}
