package com.umust.dobonglife.domain.promotion.application.dto;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDate;
import java.util.List;

public record PromotionSummary(Long promotionId,
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
                              LocalDate endDate) implements Identifiable {

    public static PromotionSummary from(Promotion promotion) {
        return new PromotionSummary(
                promotion.getId(),
                promotion.getCategory().name(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getThumbnailUrl(),
                promotion.getImages().stream()
                        .map(img -> img.getImageUrl())
                        .toList(),
                promotion.getDiscountType().name(),
                promotion.getDiscountValue(),
                promotion.getPoint(),
                promotion.getMinPrice(),
                promotion.getMaxPrice(),
                promotion.getEndDate()
        );
    }

    @Override
    public Long getId() {
        return promotionId;
    }
}
