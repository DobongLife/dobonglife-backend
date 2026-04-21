package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.TokenStore;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService implements AuthTokenUseCase {

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiredIn;

    private static final String LOGOUT_VALUE = "logout";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;
    private final TokenIssuanceHelper tokenIssuanceHelper;

    @Override
    public void logout(String accessToken, String refreshToken) {
        jwtTokenProvider.validateToken(accessToken);
        jwtTokenProvider.validateToken(refreshToken);

        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_REFRESH_TYPE);
        }

        tokenIssuanceHelper.deleteRefreshToken(refreshToken);
        invalidateAccessToken(accessToken);
    }

    @Override
    public void invalidateAccessToken(String accessToken) {
        tokenStore.store(accessToken, LOGOUT_VALUE, Duration.ofMillis(accessTokenExpiredIn));
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        tokenIssuanceHelper.deleteRefreshToken(refreshToken);
    }

    @Override
    public Long extractUserId(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);
        return jwtTokenProvider.getUserId(refreshToken);
    }

    @Override
    public AuthTokens reissueTokens(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);

        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_REFRESH_TYPE);
        }

        tokenIssuanceHelper.validateStoredRefreshToken(refreshToken);

        AuthTokens tokens = tokenIssuanceHelper.issueAndStore(
                jwtTokenProvider.getUserId(refreshToken),
                jwtTokenProvider.getProvider(refreshToken),
                jwtTokenProvider.getRole(refreshToken),
                jwtTokenProvider.getName(refreshToken)
        );

        tokenIssuanceHelper.deleteRefreshToken(refreshToken);
        return tokens;
    }
}
