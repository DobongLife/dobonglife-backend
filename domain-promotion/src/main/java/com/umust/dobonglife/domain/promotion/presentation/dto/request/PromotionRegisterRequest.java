package com.umust.dobonglife.domain.promotion.presentation.dto.request;

import com.umust.dobonglife.domain.promotion.domain.constant.DiscountType;
import com.umust.dobonglife.global.common.constant.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PromotionRegisterRequest(
        @NotBlank @Size(max = 50) String title,
        @NotBlank String description,
        @Min(0) Long point,
        @NotNull Category category,
        @NotNull DiscountType discountType,
        @NotNull @Min(0) Long discountValue,
        @Min(0) Long minPrice,
        @Min(0) Long maxPrice,
        @NotNull @Min(1) Long totalQuantity,
        @Min(1) Integer validPeriod,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate) {
}
