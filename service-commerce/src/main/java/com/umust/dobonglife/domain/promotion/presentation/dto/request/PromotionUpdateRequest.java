package com.umust.dobonglife.domain.promotion.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PromotionUpdateRequest(
        @NotBlank @Size(max = 50) String title,
        @NotBlank String description,
        @NotNull @Min(1) Long totalQuantity) {
}
