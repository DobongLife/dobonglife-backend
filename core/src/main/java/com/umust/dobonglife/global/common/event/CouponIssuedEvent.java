package com.umust.dobonglife.global.common.event;

public record CouponIssuedEvent(
        Long sagaId,
        Long userId,
        Long promotionId,
        Long couponId,
        Long pointAmount
) {

    public static CouponIssuedEvent of(Long sagaId, Long userId, Long promotionId, Long couponId, Long pointAmount) {
        return new CouponIssuedEvent(sagaId, userId, promotionId, couponId, pointAmount);
    }
}
