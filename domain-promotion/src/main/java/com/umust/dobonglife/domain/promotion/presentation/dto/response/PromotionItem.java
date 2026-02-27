package com.umust.dobonglife.domain.promotion.presentation.dto.response;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDate;
import java.util.List;

public record PromotionItem(
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

    public static PromotionItem from(Promotion promotion) {
        return new PromotionItem(
                promotion.getId(),
                promotion.getCategory().name(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getThumbnail() != null ? promotion.getThumbnail().getImageUrl() : null,
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
