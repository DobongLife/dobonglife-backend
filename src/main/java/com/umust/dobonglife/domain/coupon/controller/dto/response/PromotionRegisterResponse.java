package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PromotionRegisterResponse(
        Long promotionId,
        String couponName,
        String code,
        String discountType,
        BigDecimal discountValue,
        LocalDate startDate,
        LocalDate endDate,
        List<String> imageUrls
) {
    public static PromotionRegisterResponse from(Promotion promotion) {
        return new PromotionRegisterResponse(
                promotion.getId(),
                promotion.getTitle(),
                promotion.getCode(),
                promotion.getDiscountType().name(),
                promotion.getDiscountValue(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getImgUrls()
        );
    }
}
