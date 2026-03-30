package com.umust.dobonglife.domain.coupon.application.port.in;

import com.umust.dobonglife.domain.coupon.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.coupon.application.dto.ExchangeResponse;

public interface ExchangeUseCase {

    ExchangeResponse execute(ExchangeRequest request);
}
