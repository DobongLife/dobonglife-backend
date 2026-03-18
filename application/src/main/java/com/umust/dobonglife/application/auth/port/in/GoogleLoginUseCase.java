package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;

public interface GoogleLoginUseCase {
    AuthTokens login(String idToken, String fcmToken);
}
