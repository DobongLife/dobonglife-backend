package com.umust.dobonglife.global.port.auth.in;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.auth.dto.LoginSuccessCommand;

public interface LoginSuccessUseCase {
    AuthTokens issueLoginToken(Long userId, Provider provider, Role role, String name);
    AuthTokens handleLoginSuccess(LoginSuccessCommand command);
}
