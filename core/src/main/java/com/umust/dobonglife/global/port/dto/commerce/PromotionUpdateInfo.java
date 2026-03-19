package com.umust.dobonglife.global.port.dto.commerce;

public record PromotionUpdateInfo(
        String title,
        String description,
        Long totalQuantity
) {}
