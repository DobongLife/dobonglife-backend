package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.global.common.Identifiable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionSummaryItem(Long promotionId, String category, String title, List<String> imgUrls) implements Identifiable {
    public static PromotionSummaryItem from(Promotion promotion) {
        return new PromotionSummaryItem(
                promotion.getId(),
                promotion.getCategory().getDescription(),
                promotion.getTitle(),
                promotion.getImgUrls()
        );
    }

    @Override
    public Long getId() {
        return promotionId;
    }
}
