package com.umust.dobonglife.domain.auth.application.port.in;

public interface LogoutUseCase {

    void logout(String accessToken, String refreshToken);
}
