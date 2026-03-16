package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.IssueTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.LoginSuccessCommand;
import com.umust.dobonglife.domain.auth.application.port.out.TokenStore;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenIssueService implements IssueTokenUseCase {

    private final AuthUserPort authUserPort;
    private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;

    @Override
    public AuthTokens issueLoginToken(Long userId, Provider provider, Role role, String name) {
        String roleString = Role.PREFIX + role.name();
        return issueAndStore(userId, provider.getValue(), roleString, name);
    }

    @Override
    public AuthTokens handleLoginSuccess(LoginSuccessCommand command) {
        if (command.fcmToken() != null && !command.fcmToken().isBlank()) {
            authUserPort.updateFcmToken(command.userId(), command.fcmToken());
        }

        return issueAndStore(
                command.userId(), command.provider(), command.role(), command.userName()
        );
    }

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiredIn;

    public AuthTokens issueAndStore(Long userId, String provider, String role, String name) {
        String access = jwtTokenProvider.createAccessToken(userId, provider, role, name);
        String refresh = jwtTokenProvider.createRefreshToken(userId, provider, role, name);

        storeRefreshToken(refresh, userId);

        return new AuthTokens(access, refresh, role);
    }

    public void storeRefreshToken(String refreshToken, Long userId) {
        tokenStore.store(REFRESH_TOKEN_KEY_PREFIX + refreshToken, String.valueOf(userId),
                Duration.ofMillis(refreshTokenExpiredIn));
    }

    public void deleteRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomJwtException(AuthErrorCode.INVALID_REFRESH_TYPE);
        }
        tokenStore.delete(REFRESH_TOKEN_KEY_PREFIX + refreshToken);
    }

    public void validateStoredRefreshToken(String refreshToken) {
        String stored = tokenStore.find(REFRESH_TOKEN_KEY_PREFIX + refreshToken).orElse(null);
        if (stored == null || "false".equals(stored)) {
            throw new CustomJwtException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }
}
