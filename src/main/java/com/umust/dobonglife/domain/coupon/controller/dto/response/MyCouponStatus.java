package com.umust.dobonglife.domain.coupon.controller.dto.response;

public record MyCouponStatus(int available,
                             int used,
                             int expired) {
}
