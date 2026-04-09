package com.umust.dobonglife.domain.promotion.presentation.dto.response;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
public record PromotionUpdateResponse(String title,
                                      String description,
                                      Long totalQuantity) {
    public static PromotionUpdateResponse from(Promotion promotion) {
        return new PromotionUpdateResponse(
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getTotalQuantity()
        );
    }
}

