package com.umust.dobonglife.global.composition.dto.response;

public record CouponUsedResponse(Long couponId) {
    public static CouponUsedResponse of(Long couponId){
        return new CouponUsedResponse(couponId);
    }
}
