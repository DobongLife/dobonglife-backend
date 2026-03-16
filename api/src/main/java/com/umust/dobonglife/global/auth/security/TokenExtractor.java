package com.umust.dobonglife.global.auth.security;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface TokenExtractor {
    String extractAccessToken(HttpServletRequest request);
    Optional<String> extractAccessTokenOptional(HttpServletRequest request);
    String extractRefreshToken(HttpServletRequest request);
    Optional<String> extractRefreshTokenOptional(HttpServletRequest request);
}
