package com.umust.dobonglife.domain.coupon.controller.dto.request;

import java.time.LocalDate;
import java.util.List;

public record PromotionRegisterRequest(
        String couponName,
        String couponDescription,
        String categoryId,
        String imageUrls, // TODO: 프리셋 이미지

        String discountType,
        Integer discountValue,
        Integer minPurchaseAmount,
        Integer maxDiscountAmount,
        Integer totalQuantity,

        LocalDate usableStartDate,
        Integer validityDays,
        LocalDate issueStartDate,
        LocalDate issueEndDate) {
}
