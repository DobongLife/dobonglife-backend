package com.umust.dobonglife.domain.promotion.presentation.dto.response;

import com.umust.dobonglife.domain.promotion.domain.entity.Preset;

import java.time.LocalDate;

public record PromotionPresetResponse(
        Long id,
        String category,
        String description,
        Long point,
        String imageUrl,
        String discountType,
        Long discountValue,
        Long minPrice,
        Long maxPrice,
        Long validPeriod,
        LocalDate issueStartDate,
        LocalDate issueEndDate
) {
    public static PromotionPresetResponse from(Preset preset) {
        LocalDate now = LocalDate.now();
        return new PromotionPresetResponse(
                preset.getId(),
                preset.getCategory().name(),
                preset.getDescription(),
                preset.getPoint(),
                preset.getImageUrl(),
                preset.getDiscountType().name(),
                preset.getDiscountValue(),
                preset.getMinPrice(),
                preset.getMaxPrice(),
                preset.getValidPeriod(),
                now,
                now.plusDays(30)
        );
    }
}
