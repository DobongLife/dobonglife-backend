package com.umust.dobonglife.global.port.dto.commerce;

import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDate;
import java.util.List;

public record PromotionSummaryInfo(
        Long promotionId,
        String category,
        String title,
        String description,
        String thumbnailUrl,
        List<String> imageUrls,
        String discountType,
        Long discountValue,
        Long point,
        Long minPrice,
        Long maxPrice,
        LocalDate endDate
) implements Identifiable {
    @Override
    public Long getId() { return promotionId; }
}
