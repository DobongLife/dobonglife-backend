package com.umust.dobonglife.domain.exchange.presentation;

import com.umust.dobonglife.domain.exchange.application.port.in.ExchangeUseCase;
import com.umust.dobonglife.domain.exchange.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.exchange.application.dto.ExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/exchange")
@RequiredArgsConstructor
public class InternalExchangeController {

    private final ExchangeUseCase exchangeUseCase;

    @PostMapping
    public Map<String, Object> exchange(
            @RequestParam Long userId,
            @RequestParam Long promotionId) {
        ExchangeRequest request = new ExchangeRequest(userId, promotionId);
        ExchangeResponse response = exchangeUseCase.execute(request);
        return Map.of("couponId", response.couponId());
    }
}
