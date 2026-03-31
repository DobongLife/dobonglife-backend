package com.umust.dobonglife.common.security.extractor;

import com.umust.dobonglife.common.security.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BearerTokenExtractor implements TokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    @Override
    public String extractAccessToken(HttpServletRequest request) {
        return extractAccessTokenOptional(request)
                .orElseThrow(() -> new CustomAuthenticationException(AuthErrorCode.SECURITY_INVALID_ACCESS_TOKEN));
    }

    @Override
    public Optional<String> extractAccessTokenOptional(HttpServletRequest request) {
        return extractFromHeader(request, accessHeader);
    }

    @Override
    public String extractRefreshToken(HttpServletRequest request) {
        return extractRefreshTokenOptional(request)
                .orElseThrow(() -> new CustomAuthenticationException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    @Override
    public Optional<String> extractRefreshTokenOptional(HttpServletRequest request) {
        return extractFromHeader(request, refreshHeader);
    }

    private Optional<String> extractFromHeader(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(value -> value.startsWith(BEARER_PREFIX))
                .map(value -> value.substring(BEARER_PREFIX.length()));
    }
}
