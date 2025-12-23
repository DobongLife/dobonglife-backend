package com.umust.dobonglife.domain.auth.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenRequest {

    @NotNull(message = "Refresh Token이 없습니다")
    private String refreshToken;
}
