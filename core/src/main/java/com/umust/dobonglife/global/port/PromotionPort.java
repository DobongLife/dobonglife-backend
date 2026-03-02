package com.umust.dobonglife.global.port;

import com.umust.dobonglife.global.port.dto.PromotionInfo;

import java.util.List;
import java.util.Map;

public interface PromotionPort {

    PromotionInfo getActivePromotion(Long promotionId);

    Map<Long, PromotionInfo> getPromotionsByIds(List<Long> promotionIds);

    void tryIssueCoupon(Long sagaId, Long promotionId);

    void restoreStock(Long sagaId, Long promotionId);
}
