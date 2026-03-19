package com.umust.dobonglife.domain.coupon.presentation;

import com.umust.dobonglife.domain.coupon.application.ExchangeOrchestrator;
import com.umust.dobonglife.domain.coupon.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.coupon.application.dto.ExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/exchange")
@RequiredArgsConstructor
public class InternalExchangeController {

    private final ExchangeOrchestrator exchangeOrchestrator;

    @PostMapping
    public Map<String, Object> exchange(
            @RequestParam Long userId,
            @RequestParam Long promotionId) {
        ExchangeRequest request = new ExchangeRequest(userId, promotionId);
        ExchangeResponse response = exchangeOrchestrator.execute(request);
        return Map.of(
                "sagaId", response.sagaId(),
                "status", response.status()
        );
    }
}
