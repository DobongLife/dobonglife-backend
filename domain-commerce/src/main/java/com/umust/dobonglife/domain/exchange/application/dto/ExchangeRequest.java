package com.umust.dobonglife.domain.exchange.application.dto;

public record ExchangeRequest(
        Long userId,
        Long promotionId
) {
}
