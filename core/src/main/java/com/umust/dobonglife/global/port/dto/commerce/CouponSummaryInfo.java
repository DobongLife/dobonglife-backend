package com.umust.dobonglife.global.port.dto.commerce;

import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDate;
import java.util.List;

public record CouponSummaryInfo(
        Long couponId,
        Long promotionId,
        String category,
        String title,
        String description,
        List<String> imageUrls,
        String discountType,
        Long discountValue,
        Long minPrice,
        Long maxPrice,
        LocalDate issueEndDate,
        String couponStatus
) implements Identifiable {
    @Override
    public Long getId() { return couponId; }
}
