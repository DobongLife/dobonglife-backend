package com.umust.dobonglife.domain.user.application.service;

import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.domain.user.application.port.in.*;
import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.application.port.out.SaveUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.exception.UserErrorCode;
import com.umust.dobonglife.domain.user.exception.UserException;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService implements SignUpUseCase, DeleteAccountUseCase,
        UpdatePasswordUseCase, OAuthUserUseCase, ManageUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;
    private final CheckAuthCodeUseCase checkAuthCodeUseCase;

    // ── SignUpUseCase ──

    @Override
    public void signUp(String email, String name, String password) {
        if (loadUserPort.existsByEmail(email)) {
            throw new UserException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }
        if (!"VERIFIED".equals(checkAuthCodeUseCase.getStoredSignUpCode(email))) {
            throw new UserException(UserErrorCode.AUTH_CODE_UNAUTHORIZED);
        }
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .provider(Provider.LOCAL)
                .role(Role.MEMBER)
                .build();
        saveUserPort.save(user);
    }

    // ── DeleteAccountUseCase ──

    @Override
    public void deleteAccount(Long userId) {
        User user = loadUserPort.loadUser(userId);
        user.setFcmToken(null);
        saveUserPort.delete(user);
        SecurityContextHolder.clearContext();
    }

    @Override
    public void handleDeletion(Long userId) {
        User user = loadUserPort.loadUser(userId);
        user.handleDeletion();
        saveUserPort.save(user);
    }

    // ── UpdatePasswordUseCase ──

    @Override
    public void updateMyPassword(String email, String authCode, String newPassword) {
        User user = loadUserPort.loadLocalUser(email);
        if (user.getPassword() == null) {
            throw new UserException(UserErrorCode.USER_IS_SOCIAL_LOGGED);
        }
        checkAuthCodeUseCase.checkPasswordAuthCode(email, authCode);
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    // ── OAuthUserUseCase ──

    @Override
    public OAuthLoginUser findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name) {
        User user = loadUserPort.findByProviderAndProviderId(provider, providerUserId)
                .orElseGet(() -> reactivateOrCreateOAuthUser(provider, providerUserId, email, name));
        return new OAuthLoginUser(user.getId(), user.getName(), user.getRole());
    }

    private User reactivateOrCreateOAuthUser(Provider provider, String providerId, String email, String name) {
        return loadUserPort.findInactiveByProviderAndProviderId(provider.name(), providerId)
                .map(inactiveUser -> {
                    inactiveUser.setStatus(BaseStatus.ACTIVE);
                    inactiveUser.setEmail(email);
                    inactiveUser.setDeleteCount(0);
                    inactiveUser.setBlocked(false);
                    inactiveUser.setBlockedAt(null);
                    inactiveUser.setFcmToken(null);
                    inactiveUser.setReceivedAlarm(true);
                    return saveUserPort.save(inactiveUser);
                })
                .orElseGet(() -> createOAuthUserSafely(provider, providerId, email, name));
    }

    private User createOAuthUserSafely(Provider provider, String providerId, String email, String name) {
        try {
            if (loadUserPort.existsByEmail(email)) {
                throw new UserException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
            }

            User user = User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .name(name != null ? name : "이름 없는 사용자")
                    .role(Role.MEMBER)
                    .build();

            return saveUserPort.save(user);

        } catch (DataIntegrityViolationException e) {
            return loadUserPort.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> e);
        }
    }

    // ── ManageUserUseCase ──

    @Override
    public void canExchangeCoupon(Long userId) {
        User user = loadUserPort.loadUser(userId);
        if (!user.canExchangeCoupon()) {
            throw new UserException(UserErrorCode.COUPON_EXCHANGE_RESTRICTED);
        }
    }

    @Override
    public void updateNotificationSetting(Long userId, boolean enabled) {
        User user = loadUserPort.loadUser(userId);
        user.updateNotificationEnabled(enabled);
    }

    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = loadUserPort.loadUser(userId);
        user.setFcmToken(fcmToken);
    }

    @Override
    public void updateProviderToken(Long userId, String providerToken) {
        User user = loadUserPort.loadUser(userId);
        user.setProviderToken(providerToken);
        saveUserPort.saveAndFlush(user);
    }

    @Override
    public void inValidFcmToken(Long userId) {
        User user = loadUserPort.loadUser(userId);
        user.setFcmToken(null);
    }
}
