package com.umust.dobonglife.domain.coupon.application.port.out;

import com.umust.dobonglife.domain.coupon.domain.entity.ExchangeSaga;

public interface SaveExchangeSagaPort {

    ExchangeSaga save(ExchangeSaga saga);
}
