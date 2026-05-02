package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.port.commerce.ExchangePort;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExchangeFacade {

    private final ManageUserUseCase manageUserUseCase;
    private final ExchangePort exchangePort;

    @CircuitBreaker(name = "commerce-service")
    public Map<String, Object> exchange(Long userId, Long promotionId) {
        manageUserUseCase.canExchangeCoupon(userId);
        return exchangePort.exchange(userId, promotionId);
    }
}
