package com.umust.dobonglife.domain.coupon.application.port.in;

public interface ManageCouponUseCase {

    Long issue(Long userId, Long promotionId, int couponValidDays);

    void cancel(Long couponId);

    void useCoupon(Long couponId, Long userId);
}
