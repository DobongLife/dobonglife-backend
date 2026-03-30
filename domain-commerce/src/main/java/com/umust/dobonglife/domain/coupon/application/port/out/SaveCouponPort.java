package com.umust.dobonglife.domain.coupon.application.port.out;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;

public interface SaveCouponPort {

    Coupon save(Coupon coupon);
}
