package com.umust.dobonglife.domain.auth.application.port.in;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface ExtractTokenUseCase {

    String extractAccessToken(HttpServletRequest request);

    Optional<String> extractRefreshToken(HttpServletRequest request);

    void validateToken(String token);

    Long getUserId(String token);
}
