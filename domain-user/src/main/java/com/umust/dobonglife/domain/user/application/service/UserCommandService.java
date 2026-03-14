package com.umust.dobonglife.domain.user.application.service;

import com.umust.dobonglife.domain.user.application.port.in.*;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.infrastructure.UserJpaRepository;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
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
        UpdatePasswordUseCase, OAuthUserUseCase, ManageUserUseCase, GetUserUseCase {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final CheckAuthCodeUseCase checkAuthCodeUseCase;

    // ── SignUpUseCase ──

    @Override
    public void signUp(String email, String name, String password) {
        if (userJpaRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
        }
        if (!"VERIFIED".equals(checkAuthCodeUseCase.getStoredSignUpCode(email))) {
            throw new BusinessException(ErrorCode.AUTH_CODE_UNAUTHORIZED);
        }
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .provider(Provider.LOCAL)
                .role(Role.MEMBER)
                .build();
        userJpaRepository.save(user);
    }

    // ── DeleteAccountUseCase ──

    @Override
    public void deleteAccount(Long userId) {
        User user = findUserById(userId);
        user.setFcmToken(null);
        userJpaRepository.delete(user);
        SecurityContextHolder.clearContext();
    }

    @Override
    public void handleDeletion(Long userId) {
        User user = findUserById(userId);
        user.handleDeletion();
        userJpaRepository.save(user);
    }

    // ── UpdatePasswordUseCase ──

    @Override
    public void updateMyPassword(String email, String authCode, String newPassword) {
        User user = userJpaRepository.findByEmailAndProvider(email, Provider.LOCAL)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if (user.getPassword() == null) {
            throw new BusinessException(ErrorCode.USER_IS_SOCIAL_LOGGED);
        }
        checkAuthCodeUseCase.checkPasswordAuthCode(email, authCode);
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    // ── OAuthUserUseCase ──

    @Override
    public User findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name) {
        return userJpaRepository.findByProviderAndProviderId(provider, providerUserId)
                .orElseGet(() -> reactivateOrCreateOAuthUser(provider, providerUserId, email, name));
    }

    private User reactivateOrCreateOAuthUser(Provider provider, String providerId, String email, String name) {
        return userJpaRepository.findInactiveByProviderAndProviderId(provider.name(), providerId)
                .map(inactiveUser -> {
                    inactiveUser.setStatus(BaseStatus.ACTIVE);
                    inactiveUser.setEmail(email);
                    inactiveUser.setDeleteCount(0);
                    inactiveUser.setBlocked(false);
                    inactiveUser.setBlockedAt(null);
                    inactiveUser.setFcmToken(null);
                    inactiveUser.setReceivedAlarm(true);
                    return userJpaRepository.save(inactiveUser);
                })
                .orElseGet(() -> createOAuthUserSafely(provider, providerId, email, name));
    }

    private User createOAuthUserSafely(Provider provider, String providerId, String email, String name) {
        try {
            if (userJpaRepository.existsByEmail(email)) {
                throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
            }

            User user = User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .name(name != null ? name : "이름 없는 사용자")
                    .role(Role.MEMBER)
                    .build();

            return userJpaRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            return userJpaRepository.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> e);
        }
    }

    // ── ManageUserUseCase ──

    @Override
    public void canExchangeCoupon(Long userId) {
        User user = findUserById(userId);
        if (!user.canExchangeCoupon()) {
            throw new BusinessException(ErrorCode.COUPON_EXCHANGE_RESTRICTED);
        }
    }

    @Override
    public void updateNotificationSetting(Long userId, boolean enabled) {
        User user = findUserById(userId);
        user.updateNotificationEnabled(enabled);
    }

    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = findUserById(userId);
        user.setFcmToken(fcmToken);
    }

    @Override
    public void updateProviderToken(Long userId, String providerToken) {
        User user = findUserById(userId);
        user.setProviderToken(providerToken);
        userJpaRepository.saveAndFlush(user);
    }

    @Override
    public void inValidFcmToken(Long userId) {
        User user = findUserById(userId);
        user.setFcmToken(null);
    }

    // ── GetUserUseCase ──

    @Override
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlockedUser(Long userId) {
        return findById(userId).isBlocked();
    }

    @Override
    @Transactional(readOnly = true)
    public String getFcmToken(Long userId) {
        return findById(userId).getFcmToken();
    }

    // ── internal ──

    private User findUserById(Long userId) {
        return findById(userId);
    }
}
