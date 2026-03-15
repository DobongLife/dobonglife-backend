package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import jakarta.servlet.http.HttpServletRequest;

public interface ReissueTokenUseCase {
    AuthTokens reissueTokens(HttpServletRequest request, Long userId);
}
