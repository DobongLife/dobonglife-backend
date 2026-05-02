package com.umust.dobonglife.domain.coupon.application.port.out;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.global.common.model.BaseStatus;

public interface SaveCouponPort {

    Coupon save(Coupon coupon);

    void updateStatusByUserId(Long userId, BaseStatus currentStatus, BaseStatus newStatus);

    void deleteByUserIdAndStatus(Long userId, BaseStatus status);
}
