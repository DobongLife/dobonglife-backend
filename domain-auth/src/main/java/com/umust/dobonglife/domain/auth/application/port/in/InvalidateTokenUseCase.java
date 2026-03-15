package com.umust.dobonglife.domain.auth.application.port.in;

public interface InvalidateTokenUseCase {

    void invalidateAccessToken(String accessToken);

    void deleteRefreshToken(String refreshToken);
}
