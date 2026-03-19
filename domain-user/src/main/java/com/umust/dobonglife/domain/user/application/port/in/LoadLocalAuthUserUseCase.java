package com.umust.dobonglife.domain.user.application.port.in;

import com.umust.dobonglife.domain.user.application.dto.LocalLoginUser;

public interface LoadLocalAuthUserUseCase {
    LocalLoginUser loadLocalUserByEmail(String email);
}