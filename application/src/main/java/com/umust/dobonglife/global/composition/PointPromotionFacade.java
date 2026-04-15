package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.PointPageResponse;
import com.umust.dobonglife.global.port.commerce.PointPort;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointPromotionFacade {

    private final PointPort pointPort;
    private final PromotionPort promotionPort;

    @CircuitBreaker(name = "commerce-service", fallbackMethod = "getPointPageFallback")
    public PointPageResponse getPointPage(Long userId, Long lastId, int size) {
        CompletableFuture<Long> pointFuture =
                CompletableFuture.supplyAsync(() -> pointPort.getUserPoint(userId));
        CompletableFuture<CursorResponse<PromotionAdInfo>> promotionsFuture =
                CompletableFuture.supplyAsync(() -> promotionPort.getAdPromotions(lastId, size));

        return new PointPageResponse(pointFuture.join(), promotionsFuture.join());
    }

    private PointPageResponse getPointPageFallback(Long userId, Long lastId, int size, Exception e) {
        log.warn("[PointPromotionFacade] 서비스 호출 실패", e);
        return new PointPageResponse(0L, CursorResponse.empty());
    }
}
