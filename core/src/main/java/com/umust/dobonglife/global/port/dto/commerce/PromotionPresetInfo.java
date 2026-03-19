package com.umust.dobonglife.global.port.dto.commerce;

import java.time.LocalDate;

public record PromotionPresetInfo(
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
) {}
