package com.umust.dobonglife.domain.coupon.controller.dto.request;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.constant.PromotionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record PromotionRegisterRequest(
        String couponName,
        String couponDescription,
        @Schema(
                description = "프로모션 카테고리",
                implementation = PromotionType.class,
                example = "RESTAURANT"
        )
        String category,

        @Schema(
                description = "할인 유형",
                implementation = DiscountType.class,
                example = "PERCENT"
        )
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
