package com.umust.dobonglife.domain.exchange.application.port.in;

import com.umust.dobonglife.domain.exchange.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.exchange.application.dto.ExchangeResponse;

public interface ExchangeUseCase {

    ExchangeResponse execute(ExchangeRequest request);
}
