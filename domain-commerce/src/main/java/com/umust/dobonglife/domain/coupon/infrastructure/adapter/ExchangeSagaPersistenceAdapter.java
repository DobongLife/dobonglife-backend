package com.umust.dobonglife.domain.coupon.infrastructure.adapter;

import com.umust.dobonglife.domain.coupon.application.port.out.SaveExchangeSagaPort;
import com.umust.dobonglife.domain.coupon.domain.entity.ExchangeSaga;
import com.umust.dobonglife.domain.coupon.infrastructure.jpa.ExchangeSagaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeSagaPersistenceAdapter implements SaveExchangeSagaPort {

    private final ExchangeSagaJpaRepository exchangeSagaJpaRepository;

    @Override
    public ExchangeSaga save(ExchangeSaga saga) {
        return exchangeSagaJpaRepository.save(saga);
    }
}
