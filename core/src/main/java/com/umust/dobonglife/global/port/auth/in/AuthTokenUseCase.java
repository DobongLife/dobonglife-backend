package com.umust.dobonglife.global.port.auth.in;

import com.umust.dobonglife.global.port.auth.dto.AuthTokens;

public interface AuthTokenUseCase {
    void logout(String accessToken, String refreshToken);
    void invalidateAccessToken(String accessToken);
    void deleteRefreshToken(String refreshToken);
    AuthTokens reissueTokens(String refreshToken);
}
