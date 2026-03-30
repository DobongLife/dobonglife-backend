package com.umust.dobonglife.domain.exchange.application.port.out;

import com.umust.dobonglife.domain.exchange.domain.entity.ExchangeSaga;

public interface SaveExchangeSagaPort {

    ExchangeSaga save(ExchangeSaga saga);
}
