package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.global.common.Identifiable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionBannerItem(
        Long promotionId, String category, String title, String description, List<String> imgUrls
) implements Identifiable {
    public static PromotionBannerItem from(Promotion promotion) {
        return new PromotionBannerItem(
                promotion.getId(),
                promotion.getCategory().getDescription(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getImgUrls()
        );
    }

    @Override
    public Long getId() {
        return promotionId;
    }
}