package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import com.umust.dobonglife.domain.coupon.presentation.dto.CouponSummary;
import com.umust.dobonglife.domain.coupon.presentation.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyCouponGetResponse(MyCouponStatus myCouponStatus,
                                  CursorResponse<CouponSummary> myCouponList) {
}
