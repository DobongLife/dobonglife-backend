package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import java.util.List;

public record MyCouponResponse(MyCouponStatus myCouponStatus,
                               List<CouponItem> myCouponList) {
}
