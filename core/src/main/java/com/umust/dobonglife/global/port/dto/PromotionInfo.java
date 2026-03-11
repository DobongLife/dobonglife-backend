package com.umust.dobonglife.global.port.dto;

import java.time.LocalDate;
import java.util.List;

public record PromotionInfo(
        Long promotionId,
        String category,
        String title,
        String description,
        String thumbnailUrl,
        List<String> imageUrls,
        String discountType,
        Long discountValue,
        Long minPrice,
        Long maxPrice,
        Long point,
        Integer couponValidDays,
        LocalDate startDate,
        LocalDate endDate
) {
}
