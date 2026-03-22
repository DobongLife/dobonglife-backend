package com.umust.dobonglife.application.coupon.dto;

import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyCouponGetResponse(MyCouponStatus myCouponStatus,
                                  CursorResponse<CouponSummary> myCouponList) {
}
