package com.umust.dobonglife.domain.coupon.controller.dto.request;

public record CouponCodeRequest(Long couponId,
                                Long promotionId,
                                String code) {
}
