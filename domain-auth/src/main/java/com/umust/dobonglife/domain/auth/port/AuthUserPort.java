package com.umust.dobonglife.domain.auth.port;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;

import java.util.Optional;

public interface AuthUserPort {
    AuthUserInfo findOrCreateOAuthUser(Provider provider, String providerId, String email, String name);
    Optional<AuthUserInfo> findByEmailAndLocalProvider(String email);
    Optional<AuthUserInfo> findByProviderId(String providerId);
    AuthUserInfo findById(Long userId);
    void updateFcmToken(Long userId, String fcmToken);
    void invalidateFcmToken(Long userId);
    void updateProviderToken(Long userId, String providerToken);

    record AuthUserInfo(Long id, String email, String name, String password,
                        Role role, Provider provider, String providerId,
                        String providerToken, String fcmToken) {}
}
