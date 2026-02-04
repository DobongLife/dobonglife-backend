package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.preset.domain.Preset;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PresetResponse(
        Long id,
        String category,
        String couponDescription,
        Long point,
        String img,
        String discountType,
        Integer discountValue,
        Long minPurchaseAmount,
        Long maxDiscountAmount,
        Long validityDays,
        LocalDate issueStartDate,
        LocalDate issueEndDate
) {
    public static PresetResponse from(Preset preset) {
        LocalDate now = LocalDate.now();

        return new PresetResponse(
                preset.getId(),
                preset.getCategory(),
                preset.getDescription(),
                preset.getPoint(),
                preset.getImg(),
                preset.getDiscountType().toString(),
                preset.getDiscountValue().intValue(),
                preset.getMinPrice(),
                preset.getMaxPrice(),
                preset.getValidPeriod(),
                now,
                now.plusDays(30)
        );
    }
}
