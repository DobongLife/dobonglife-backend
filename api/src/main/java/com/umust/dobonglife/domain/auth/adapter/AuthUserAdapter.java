package com.umust.dobonglife.domain.auth.adapter;

import com.umust.dobonglife.domain.auth.port.AuthUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthUserAdapter implements AuthUserPort {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public AuthUserInfo findOrCreateOAuthUser(Provider provider, String providerId, String email, String name) {
        User user = userService.findOrCreateOAuthUser(provider, providerId, email, name);
        return toAuthUserInfo(user);
    }

    @Override
    public Optional<AuthUserInfo> findByEmailAndLocalProvider(String email) {
        return userRepository.findByEmailAndProvider(email, Provider.LOCAL)
                .map(this::toAuthUserInfo);
    }

    @Override
    public Optional<AuthUserInfo> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId)
                .map(this::toAuthUserInfo);
    }

    @Override
    public AuthUserInfo findById(Long userId) {
        User user = userService.findById(userId);
        return toAuthUserInfo(user);
    }

    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        userService.updateFcmToken(userId, fcmToken);
    }

    @Override
    public void invalidateFcmToken(Long userId) {
        userService.inValidFcmToken(userId);
    }

    @Override
    public void updateProviderToken(Long userId, String providerToken) {
        userService.updateProviderToken(userId, providerToken);
    }

    private AuthUserInfo toAuthUserInfo(User user) {
        return new AuthUserInfo(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getRole(),
                user.getProvider(),
                user.getProviderId(),
                user.getProviderToken(),
                user.getFcmToken()
        );
    }
}
