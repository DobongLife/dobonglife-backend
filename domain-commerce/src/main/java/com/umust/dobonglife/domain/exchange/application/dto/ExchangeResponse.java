package com.umust.dobonglife.domain.exchange.application.dto;

import com.umust.dobonglife.domain.exchange.domain.entity.ExchangeSaga;

public record ExchangeResponse(
        Long sagaId,
        Long couponId,
        String status
) {

    public static ExchangeResponse from(ExchangeSaga saga) {
        return new ExchangeResponse(saga.getId(), saga.getCouponId(), saga.getSagaStatus().name());
    }
}
