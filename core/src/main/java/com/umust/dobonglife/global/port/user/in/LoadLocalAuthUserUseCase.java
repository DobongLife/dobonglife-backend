package com.umust.dobonglife.global.port.user.in;

import com.umust.dobonglife.global.port.user.dto.LocalLoginUser;

public interface LoadLocalAuthUserUseCase {
    LocalLoginUser loadLocalUserByEmail(String email);
}
