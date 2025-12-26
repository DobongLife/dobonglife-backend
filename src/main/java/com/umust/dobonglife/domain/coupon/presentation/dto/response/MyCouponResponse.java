package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;

public record MyCouponResponse(MyCouponStatus myCouponStatus,
                               CursorResponse<CouponItem> myCouponList) {
}
