package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.global.port.auth.dto.AuthTokens;

public interface KakaoLoginUseCase {
    AuthTokens login(String accessToken, String fcmToken);
}
