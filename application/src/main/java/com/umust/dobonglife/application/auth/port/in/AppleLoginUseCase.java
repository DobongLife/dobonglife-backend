package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.dto.request.AppleLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;

public interface AppleLoginUseCase {
    TokenResponse login(AppleLoginRequest request);
}
