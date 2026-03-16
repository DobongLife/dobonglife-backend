package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.out.TokenStore;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenIssuanceHelper {

    private static final String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh:";

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiredIn;

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;


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
