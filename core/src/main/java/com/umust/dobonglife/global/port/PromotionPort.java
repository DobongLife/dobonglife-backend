package com.umust.dobonglife.global.port;

import com.umust.dobonglife.global.port.dto.PromotionInfo;

public interface PromotionPort {

    PromotionInfo getActivePromotion(Long promotionId);

    void tryIssueCoupon(Long sagaId, Long promotionId);

    void restoreStock(Long sagaId, Long promotionId);
}
