package com.umust.dobonglife.domain.promotion.infrastructure.adapter;

import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.port.PromotionPort;
import com.umust.dobonglife.global.port.dto.PromotionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionPortAdapter implements PromotionPort {

    private final PromotionService promotionService;

    @Override
    public PromotionInfo getActivePromotion(Long promotionId) {
        Promotion promotion = promotionService.getActivePromotion(promotionId);
        return new PromotionInfo(
                promotion.getId(),
                promotion.getTitle(),
                promotion.getPoint(),
                promotion.getCouponValidDays(),
                promotion.getStartDate(),
                promotion.getEndDate()
        );
    }

    @Override
    public void tryIssueCoupon(Long sagaId, Long promotionId) {
        promotionService.deductStock(promotionId);
    }

    @Override
    public void restoreStock(Long sagaId, Long promotionId) {
        promotionService.restoreStock(promotionId);
    }
}
