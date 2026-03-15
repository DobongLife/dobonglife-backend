package com.umust.dobonglife.domain.auth.application.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        String role
) {
}
