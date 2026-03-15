package com.umust.dobonglife.application.auth.port;

public interface AccountCleanupPort {
    boolean isBusiness(Long userId);
    void cleanupBusinessData(Long userId);
    void cleanupUserData(Long userId);
}
