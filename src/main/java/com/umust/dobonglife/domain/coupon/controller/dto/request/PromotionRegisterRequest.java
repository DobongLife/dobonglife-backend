package com.umust.dobonglife.domain.coupon.controller.dto.request;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.global.common.model.constant.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record PromotionRegisterRequest(
        String couponName,
        String couponDescription,
        Long point,
        @Schema(
                description = "프로모션 카테고리",
                implementation = Category.class,
                example = "RESTAURANT"
        )
        Category category,

        @Schema(
                description = "할인 유형",
                implementation = DiscountType.class,
                example = "PERCENT"
        )
        DiscountType discountType,
        Integer discountValue,
        Integer minPurchaseAmount,
        Integer maxDiscountAmount,
        Long totalQuantity,
        Integer validityDays,
        LocalDate issueStartDate,
        LocalDate issueEndDate) {
}
