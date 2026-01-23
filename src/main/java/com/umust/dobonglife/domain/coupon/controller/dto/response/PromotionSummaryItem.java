package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PromotionSummaryItem(Long promotionId, String category, String title, String img) {
    public static PromotionSummaryItem from(Promotion promotion) {
        return new PromotionSummaryItem(
                promotion.getId(),
                promotion.getCategory().getDescription(),
                promotion.getTitle(),
                promotion.getImg()
        );
    }
}
