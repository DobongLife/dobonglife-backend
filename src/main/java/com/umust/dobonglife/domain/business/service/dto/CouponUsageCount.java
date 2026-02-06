package com.umust.dobonglife.domain.business.service.dto;

public record CouponUsageCount(
        Long promotionId,
        long usedCount
) {}
