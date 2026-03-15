package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.ReissueTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.TokenStore;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import com.umust.dobonglife.global.common.error.exception.BusinessException;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements ReissueTokenUseCase {

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiredIn;

    private static final String LOGOUT_VALUE = "logout";

    private final TokenStore tokenStore;
    private final JwtTokenProvider jwtUtil;
    private final TokenIssuanceHelper tokenIssuanceHelper;

    @Override
    public AuthTokens reissueTokens(jakarta.servlet.http.HttpServletRequest request, Long userId) {
        String refreshToken = jwtUtil.extractRefreshToken(request)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));
        jwtUtil.validateToken(refreshToken);
        if (!"refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_REFRESH_TYPE);
        }
        tokenIssuanceHelper.validateStoredRefreshToken(refreshToken);
        return reissueAndSendTokens(refreshToken, userId);
    }

    public void checkLogout(String accessToken) {
        String value = tokenStore.find(accessToken).orElse(null);
        if (LOGOUT_VALUE.equals(value)) {
            throw new CustomAuthenticationException(AuthErrorCode.SECURITY_UNAUTHORIZED);
        }
    }

    public void invalidAccessToken(String accessToken) {
        tokenStore.store(accessToken, LOGOUT_VALUE, Duration.ofMillis(accessTokenExpiredIn));
    }

    private AuthTokens reissueAndSendTokens(String refreshToken, Long userId) {
        AuthTokens tokens = tokenIssuanceHelper.issueAndStore(
                jwtUtil.getUserId(refreshToken),
                jwtUtil.getProvider(refreshToken),
                jwtUtil.getRole(refreshToken),
                jwtUtil.getName(refreshToken)
        );

        tokenIssuanceHelper.deleteRefreshToken(refreshToken);

        return tokens;
    }
}
