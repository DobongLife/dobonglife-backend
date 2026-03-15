package com.umust.dobonglife.application.auth.adapter;

import com.umust.dobonglife.application.auth.port.AccountCleanupPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * AccountCleanupPort 임시 구현체.
 * 각 도메인 모듈이 마이그레이션되면 실제 서비스를 주입하여 교체한다.
 */
@Slf4j
@Component
public class AccountCleanupAdapter implements AccountCleanupPort {

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
