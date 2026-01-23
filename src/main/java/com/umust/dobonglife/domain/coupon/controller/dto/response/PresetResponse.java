package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.preset.domain.Preset;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PresetResponse(
        Long id,
        String category,
        String description,
        String img,
        DiscountType discountType,
        BigDecimal discountValue,
        Long minPrice,
        Long maxPrice,
        Long point,
        Long validPeriod,
        LocalDate startDate,
        LocalDate endDate
) {
    public static PresetResponse from(Preset preset) {
        LocalDate now = LocalDate.now();

        return new PresetResponse(
                preset.getId(),
                preset.getCategory(),
                preset.getDescription(),
                preset.getImg(),
                preset.getDiscountType(),
                preset.getDiscountValue(),
                preset.getMinPrice(),
                preset.getMaxPrice(),
                preset.getPoint(),
                preset.getValidPeriod(),
                now,
                now.plusDays(30)
        );
    }
}
