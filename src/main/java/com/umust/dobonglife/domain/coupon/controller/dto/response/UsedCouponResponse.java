package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;

public record UsedCouponResponse(Long couponId,
                                 CouponStatus couponStatus) {
}
