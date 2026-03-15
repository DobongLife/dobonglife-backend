package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;

public interface AppleLoginUseCase {
    AuthTokens login(String identityToken, String fcmToken, String providerToken);
}
