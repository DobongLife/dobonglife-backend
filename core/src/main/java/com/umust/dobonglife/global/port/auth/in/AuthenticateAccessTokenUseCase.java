package com.umust.dobonglife.global.port.auth.in;

import com.umust.dobonglife.global.port.auth.dto.AuthenticatedUser;

public interface AuthenticateAccessTokenUseCase {
    AuthenticatedUser authenticate(String accessToken);
}
