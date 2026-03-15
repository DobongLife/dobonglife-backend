package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ReissueTokenUseCase {
    TokenResponse reissueTokens(HttpServletRequest request, Long userId);
}
