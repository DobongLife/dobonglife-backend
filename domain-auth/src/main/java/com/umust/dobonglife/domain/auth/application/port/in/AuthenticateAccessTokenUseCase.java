package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface AuthenticateAccessTokenUseCase {
    Optional<String> extractAccessToken(HttpServletRequest request);
    AuthenticatedUser authenticate(String accessToken);
}
