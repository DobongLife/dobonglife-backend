package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.domain.AuthTokens;

public interface GoogleLoginUseCase {
    AuthTokens login(String idToken, String fcmToken);
}
