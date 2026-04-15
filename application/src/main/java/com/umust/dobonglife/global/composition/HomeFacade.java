package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.HomeResponse;
import com.umust.dobonglife.global.port.content.BannerPort;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.content.BannerInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeFacade {

    private final BannerPort bannerPort;
    private final PromotionPort promotionPort;

    @CircuitBreaker(name = "content-service", fallbackMethod = "getHomeFallback")
    public HomeResponse getHome(Long lastId, int size) {
        CompletableFuture<List<BannerInfo>> bannersFuture =
                CompletableFuture.supplyAsync(bannerPort::getActiveBanners);
        CompletableFuture<CursorResponse<PromotionSummaryInfo>> promotionsFuture =
                CompletableFuture.supplyAsync(() -> promotionPort.getPromotions(lastId, size));

        return new HomeResponse(bannersFuture.join(), promotionsFuture.join());
    }

    private HomeResponse getHomeFallback(Long lastId, int size, Exception e) {
        log.warn("[HomeFacade] 서비스 호출 실패, 빈 응답 반환", e);
        return new HomeResponse(Collections.emptyList(), CursorResponse.empty());
    }
}
