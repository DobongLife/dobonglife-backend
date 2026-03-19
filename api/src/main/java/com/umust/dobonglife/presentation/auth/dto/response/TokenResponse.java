package com.umust.dobonglife.presentation.auth.dto.response;

import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String role;

    public static TokenResponse from(AuthTokens authTokens) {
        return TokenResponse.builder()
                .accessToken(authTokens.accessToken())
                .refreshToken(authTokens.refreshToken())
                .role(authTokens.role())
                .build();
    }
}
