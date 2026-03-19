package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;

public interface AuthTokenUseCase {

    void logout(String accessToken, String refreshToken);

    void invalidateAccessToken(String accessToken);

    void deleteRefreshToken(String refreshToken);

    AuthTokens reissueTokens(String refreshToken);
}
