package com.umust.dobonglife.application.auth.adapter;

import com.umust.dobonglife.application.auth.port.AccountCleanupPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountCleanupAdapter implements AccountCleanupPort {

    @Override
    public boolean isBusiness(Long userId) {
        // TODO: wire real BusinessService when domain-business is migrated
        log.warn("[AccountCleanup] isBusiness stub called for userId={}", userId);
        return false;
    }

    @Override
    public void cleanupBusinessData(Long userId) {
        // TODO: wire real BusinessService when domain-business is migrated
        log.warn("[AccountCleanup] cleanupBusinessData stub called for userId={}", userId);
    }

    @Override
    public void cleanupUserData(Long userId) {
        // TODO: wire real cleanup services when other domains are migrated
        log.warn("[AccountCleanup] cleanupUserData stub called for userId={}", userId);
    }
}
