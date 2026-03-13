package com.umust.dobonglife.domain.promotion.infrastructure.adapter;

import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.domain.entity.PromotionImage;
import com.umust.dobonglife.global.port.PromotionPort;
import com.umust.dobonglife.global.port.dto.PromotionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PromotionPortAdapter implements PromotionPort {

    private final PromotionService promotionService;

    @Override
    public PromotionInfo getActivePromotion(Long promotionId) {
        Promotion promotion = promotionService.getActivePromotion(promotionId);
        return toPromotionInfo(promotion);
    }

    @Override
    public Map<Long, PromotionInfo> getPromotionsByIds(List<Long> promotionIds) {
        List<Promotion> promotions = promotionService.getPromotionsByIds(promotionIds);
        return promotions.stream()
                .collect(Collectors.toMap(Promotion::getId, this::toPromotionInfo));
    }

    @Override
    public void tryIssueCoupon(Long sagaId, Long promotionId) {
        promotionService.deductStock(promotionId);
    }

    @Override
    public void restoreStock(Long sagaId, Long promotionId) {
        promotionService.restoreStock(promotionId);
    }

    @Override
    public void validateCode(Long promotionId, String code) {
        promotionService.validateCode(promotionId, code);
    }

    private PromotionInfo toPromotionInfo(Promotion promotion) {
        String thumbnailUrl = promotion.getThumbnail() != null
                ? promotion.getThumbnail().getImageUrl() : null;
        List<String> imageUrls = promotion.getImages().stream()
                .map(PromotionImage::getImageUrl)
                .toList();

        return new PromotionInfo(
                promotion.getId(),
                promotion.getCategory().name(),
                promotion.getTitle(),
                promotion.getDescription(),
                thumbnailUrl,
                imageUrls,
                promotion.getDiscountType().name(),
                promotion.getDiscountValue(),
                promotion.getMinPrice(),
                promotion.getMaxPrice(),
                promotion.getPoint(),
                promotion.getCouponValidDays(),
                promotion.getStartDate(),
                promotion.getEndDate()
        );
    }
}
