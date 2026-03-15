package com.umust.dobonglife.application.auth.port.in;

import com.umust.dobonglife.domain.auth.dto.request.KakaoLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;

public interface KakaoLoginUseCase {
    TokenResponse login(KakaoLoginRequest request);
}
