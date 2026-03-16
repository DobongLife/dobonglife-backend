package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthenticatedUser;

public interface AuthenticateAccessTokenUseCase {
    AuthenticatedUser authenticate(String accessToken);
}
