package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.global.port.auth.dto.AuthTokens;

public interface GoogleLoginUseCase {
    AuthTokens login(String idToken, String fcmToken);
}
