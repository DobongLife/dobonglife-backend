package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;

public record CouponCreatedResponse(Long couponId,
                                    CouponStatus status) {
}