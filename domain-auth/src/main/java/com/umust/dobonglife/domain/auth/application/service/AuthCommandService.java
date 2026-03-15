package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.ExtractTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.InvalidateTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.LogoutUseCase;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.JwtTokenProvider;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.global.common.error.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService implements LogoutUseCase, ExtractTokenUseCase, InvalidateTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final TokenIssuanceHelper tokenIssuanceHelper;

    // ── ExtractTokenUseCase ──

    @Override
    public String extractAccessToken(HttpServletRequest request) {
        return jwtTokenProvider.extractAccessToken(request)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.SECURITY_INVALID_ACCESS_TOKEN));
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return jwtTokenProvider.extractRefreshToken(request);
    }

    @Override
    public void validateToken(String token) {
        jwtTokenProvider.validateToken(token);
    }

    @Override
    public Long getUserId(String token) {
        return jwtTokenProvider.getUserId(token);
    }

    // ── LogoutUseCase ──

    @Override
    public void logout(String accessToken, String refreshToken) {
        jwtTokenProvider.validateToken(accessToken);
        jwtTokenProvider.validateToken(refreshToken);

        if (!"refresh".equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new CustomJwtException(AuthErrorCode.INVALID_REFRESH_TYPE);
        }

        tokenIssuanceHelper.deleteRefreshToken(refreshToken);
        jwtService.invalidAccessToken(accessToken);
    }

    // ── InvalidateTokenUseCase ──

    @Override
    public void invalidateAccessToken(String accessToken) {
        jwtService.invalidAccessToken(accessToken);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        tokenIssuanceHelper.deleteRefreshToken(refreshToken);
    }
}
