package com.umust.dobonglife.application.auth.adapter;

import com.umust.dobonglife.application.auth.port.AccountCleanupPort;
import com.umust.dobonglife.global.port.BusinessPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TODO: 각 도메인 모듈에 cleanup use case가 추가되면 여기서 호출
 * - ReviewCleanupUseCase.setNullByUserId(userId)
 * - PointCleanupUseCase.deleteByUserId(userId)
 * - CouponCleanupUseCase.deleteByUserId(userId)
 * - PromotionCleanupUseCase.deleteByUserId(userId)
 * - BusinessCleanupUseCase.deleteByUserId(userId)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCleanupAdapter implements AccountCleanupPort {

    private final BusinessPort businessPort;

    @Override
    public boolean isBusiness(Long userId) {
        return businessPort.checkBusiness(userId);
    }

    @Override
    public void cleanupBusinessData(Long userId) {
        log.warn("[AccountCleanup] cleanupBusinessData 미구현 - userId: {}", userId);
        // TODO: promotionService.deleteByUserId(userId)
        // TODO: businessService.nullifyPlace → placeService.deleteById → businessService.deleteById
    }

    @Override
    public void cleanupUserData(Long userId) {
        log.warn("[AccountCleanup] cleanupUserData 미구현 - userId: {}", userId);
        // TODO: reviewCleanup.setNullByUserId(userId)
        // TODO: pointCleanup.deleteByUserId(userId)
        // TODO: couponCleanup.deleteByUserId(userId)
    }
}
