package com.umust.dobonglife.application.coupon.dto;

public record CouponUsedResponse(Long couponId) {
    public static CouponUsedResponse of(Long couponId) {
        return new CouponUsedResponse(couponId);
    }
}
