package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.domain.AuthTokens;

public interface KakaoLoginUseCase {
    AuthTokens login(String accessToken, String fcmToken);
}
