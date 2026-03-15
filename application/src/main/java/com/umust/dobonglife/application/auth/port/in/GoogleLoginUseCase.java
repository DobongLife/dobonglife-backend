package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.dto.request.GoogleLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;

public interface GoogleLoginUseCase {
    TokenResponse login(GoogleLoginRequest request);
}
