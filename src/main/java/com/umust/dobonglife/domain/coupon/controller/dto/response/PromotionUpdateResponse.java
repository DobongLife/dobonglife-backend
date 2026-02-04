package com.umust.dobonglife.domain.coupon.controller.dto.response;

import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;

public record PromotionUpdateResponse(String couponName,
                                      String couponDescription,
                                      Long totalQuantity) {
    public static PromotionUpdateResponse from(Promotion promotion) {
        return new PromotionUpdateResponse(
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getTotalQuantity()
        );
    }
}
