package com.umust.dobonglife.global.port.commerce;

import com.umust.dobonglife.global.port.dto.commerce.MyCouponInfo;

public interface CouponPort {
    MyCouponInfo getMyCoupons(Long userId, Long lastId, int size);
    void useCoupon(Long promotionId, String code, Long couponId, Long userId);
}
