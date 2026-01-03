package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromotionItem(Long promotionId, String category, String title,
                            String description, String img, DiscountType discountType, BigDecimal discountValue,
                            Long point, LocalDate endDate) {
    public static PromotionItem from(Promotion promotion) {
        return new PromotionItem(
                promotion.getId(),
                promotion.getCategory(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getImg(),
                promotion.getDiscountType(),
                promotion.getDiscountValue(),
                promotion.getPoint(),
                promotion.getEndDate()
        );
    }
}