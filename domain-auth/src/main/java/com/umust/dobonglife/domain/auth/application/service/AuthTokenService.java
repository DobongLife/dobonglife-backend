package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.TokenStore;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.AuthenticatedUser;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.domain.auth.exception.AuthException;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService implements AuthTokenUseCase, AuthenticateAccessTokenUseCase {

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiredIn;

    private static final String LOGOUT_VALUE = "logout";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenStore tokenStore;
    private final TokenIssuanceHelper tokenIssuanceHelper;

    // ── AuthTokenUseCase ──

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

    // ── AuthenticateAccessTokenUseCase ──

    @Override
    public AuthenticatedUser authenticate(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);

        if (!"access".equals(jwtTokenProvider.getTokenType(accessToken))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_TOKEN_TYPE);
        }

        checkLogout(accessToken);

        return new AuthenticatedUser(
                jwtTokenProvider.getUserId(accessToken),
                jwtTokenProvider.getName(accessToken),
                Role.fromRole(jwtTokenProvider.getRole(accessToken)),
                Provider.fromProvider(jwtTokenProvider.getProvider(accessToken))
        );
    }

    private void checkLogout(String accessToken) {
        String value = tokenStore.find(accessToken).orElse(null);
        if (LOGOUT_VALUE.equals(value)) {
            throw new AuthException(AuthErrorCode.SECURITY_UNAUTHORIZED);
        }
    }
}
