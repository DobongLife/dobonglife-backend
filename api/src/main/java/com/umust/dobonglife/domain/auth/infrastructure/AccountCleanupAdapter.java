package com.umust.dobonglife.domain.auth.infrastructure;

import com.umust.dobonglife.domain.auth.application.port.AccountCleanupPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AccountCleanupPort 임시 구현체.
 * 각 도메인 모듈이 마이그레이션되면 실제 서비스를 주입하여 교체한다.
 */
@Slf4j
@Component
public class AccountCleanupAdapter implements AccountCleanupPort {

    // TODO: 각 도메인 모듈 마이그레이션 후 실제 서비스 주입
    // private final BusinessService businessService;
    // private final CouponService couponService;
    // private final PromotionService promotionService;
    // private final CourseService courseService;
    // private final PlaceService placeService;
    // private final PointService pointService;
    // private final ReviewService reviewService;

    @Override
    public boolean isBusiness(Long userId) {
        log.warn("[AccountCleanupAdapter] isBusiness 미구현 - 도메인 모듈 마이그레이션 필요. userId={}", userId);
        return false;
    }

    @Override
    public void cleanupBusinessData(Long userId) {
        log.warn("[AccountCleanupAdapter] cleanupBusinessData 미구현 - 도메인 모듈 마이그레이션 필요. userId={}", userId);
    }

    @Override
    public void cleanupUserData(Long userId) {
        log.warn("[AccountCleanupAdapter] cleanupUserData 미구현 - 도메인 모듈 마이그레이션 필요. userId={}", userId);
    }
}
