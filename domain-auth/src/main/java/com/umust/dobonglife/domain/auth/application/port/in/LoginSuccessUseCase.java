package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.LoginSuccessCommand;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;

public interface LoginSuccessUseCase {

    AuthTokens issueLoginToken(Long userId, Provider provider, Role role, String name);

    AuthTokens handleLoginSuccess(LoginSuccessCommand command);
}
