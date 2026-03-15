package com.umust.dobonglife.domain.auth.domain;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        String role
) {
}
