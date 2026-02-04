package com.umust.dobonglife.domain.coupon.controller.dto.request;

public record PromotionUpdateRequest(String couponName,
                                     String couponDescription,
                                     Long totalQuantity) {
}
