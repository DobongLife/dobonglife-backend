package com.umust.dobonglife.domain.coupon.application.port.in;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;

public interface GetCouponUseCase {

    CursorResponse<CouponDetail> getMyCoupons(Long userId, Long lastId, int size);

    MyCouponStatus getMyCouponStatus(Long userId);
}
