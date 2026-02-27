package com.umust.dobonglife.domain.promotion.application.dto;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.Identifiable;

public record PromotionAdSummary(
        Long promotionId,
        String category,
        String title,
        String description,
        String thumbnailUrl
) implements Identifiable {

    public static PromotionAdSummary from(Promotion promotion) {
        return new PromotionAdSummary(
                promotion.getId(),
                promotion.getCategory().name(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getThumbnail() != null ? promotion.getThumbnail().getImageUrl() : null
        );
    }

    @Override
    public Long getId () {
        return promotionId;
    }
}
