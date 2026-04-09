package com.umust.dobonglife.domain.coupon.application.dto;

public record MyCouponStatus(
        Long available,
        Long used,
        Long expired
) {
    public static MyCouponStatus of(Long available, Long used, Long expired) {
        return new MyCouponStatus(available, used, expired);
    }
}
