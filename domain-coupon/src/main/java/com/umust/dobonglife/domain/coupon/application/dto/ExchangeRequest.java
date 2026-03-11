package com.umust.dobonglife.domain.coupon.application.dto;

public record ExchangeRequest(
        Long userId,
        Long promotionId
) {
}
