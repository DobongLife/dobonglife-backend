package com.umust.dobonglife.global.common.event;

public record CouponIssuedEvent(
        Long userId,
        Long promotionId,
        Long couponId,
        Long pointAmount
) {

    public static CouponIssuedEvent of(Long userId, Long promotionId, Long couponId, Long pointAmount) {
        return new CouponIssuedEvent(userId, promotionId, couponId, pointAmount);
    }
}
