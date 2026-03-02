package com.umust.dobonglife.global.port;

import com.umust.dobonglife.global.port.dto.PromotionInfo;

public interface CouponPort {

    Long issue(Long sagaId, Long userId, PromotionInfo promotionInfo);

    void cancel(Long sagaId, Long couponId);
}
