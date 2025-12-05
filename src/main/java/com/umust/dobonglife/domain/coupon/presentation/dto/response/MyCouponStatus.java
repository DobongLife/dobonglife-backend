package com.umust.dobonglife.domain.coupon.presentation.dto.response;

public record MyCouponStatus(int available,
                             int used,
                             int expired) {
}
