package com.umust.dobonglife.global.composition.dto.response;

import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.CouponSummary;

public record MyCouponGetResponse(MyCouponStatus myCouponStatus,
                                  CursorResponse<CouponSummary> myCouponList) {
}
