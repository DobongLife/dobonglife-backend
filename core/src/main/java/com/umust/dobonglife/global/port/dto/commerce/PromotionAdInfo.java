package com.umust.dobonglife.global.port.dto.commerce;

import com.umust.dobonglife.global.common.Identifiable;

public record PromotionAdInfo(
        Long promotionId,
        String category,
        String title,
        String description,
        String thumbnailUrl
) implements Identifiable {
    @Override
    public Long getId() {
        return promotionId;
    }
}
