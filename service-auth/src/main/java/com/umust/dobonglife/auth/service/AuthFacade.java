package com.umust.dobonglife.auth.service;

import com.umust.dobonglife.auth.client.UserServiceClient;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.exception.AuthErrorCode;
import com.umust.dobonglife.domain.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthTokenUseCase authTokenUseCase;
    private final UserServiceClient userServiceClient;

    public void logout(String accessToken, String refreshToken, Long userId) {
        userServiceClient.invalidateFcmToken(userId);
        authTokenUseCase.logout(accessToken, refreshToken);
    }

    public AuthTokens reissueTokens(String refreshToken) {
        Long userId = authTokenUseCase.extractUserId(refreshToken);
        if (!userServiceClient.isActiveUser(userId)) {
            throw new AuthException(AuthErrorCode.SECURITY_UNAUTHORIZED);
        }
        return authTokenUseCase.reissueTokens(refreshToken);
    }
}
