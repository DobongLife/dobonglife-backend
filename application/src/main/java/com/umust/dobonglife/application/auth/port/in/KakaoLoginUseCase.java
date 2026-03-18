package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;

public interface KakaoLoginUseCase {
    AuthTokens login(String accessToken, String fcmToken);
}
