package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.global.port.auth.dto.AuthTokens;

public interface AppleLoginUseCase {
    AuthTokens login(String identityToken, String fcmToken, String providerToken);
}
