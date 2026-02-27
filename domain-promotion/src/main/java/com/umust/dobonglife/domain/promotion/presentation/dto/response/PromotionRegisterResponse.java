package com.umust.dobonglife.domain.promotion.presentation.dto.response;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;

import java.time.LocalDate;
import java.util.List;

public record PromotionRegisterResponse(
        Long promotionId,
        String couponName,
        String code,
        Long point,
        String discountType,
        Long discountValue,
        LocalDate startDate,
        LocalDate endDate,
        List<String> imageUrls
) {
public static PromotionRegisterResponse from(Promotion promotion) {
    return new PromotionRegisterResponse(
            promotion.getId(),
            promotion.getTitle(),
            promotion.getCode(),
            promotion.getPoint(),
            promotion.getDiscountType().name(),
            promotion.getDiscountValue(),
            promotion.getStartDate(),
            promotion.getEndDate(),
            promotion.getImages().stream()
                    .map(img -> img.getImageUrl())
                    .toList()
    );
}
}
