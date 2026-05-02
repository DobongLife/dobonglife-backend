package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.port.commerce.CouponPort;
import com.umust.dobonglife.global.port.dto.commerce.MyCouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponPort couponPort;

    public MyCouponInfo getMyCoupon(Long userId, Long lastId, int size) {
        return couponPort.getMyCoupons(userId, lastId, size);
    }

    public void useCoupon(Long promotionId, String code, Long couponId, Long userId) {
        couponPort.useCoupon(promotionId, code, couponId, userId);
    }
}
