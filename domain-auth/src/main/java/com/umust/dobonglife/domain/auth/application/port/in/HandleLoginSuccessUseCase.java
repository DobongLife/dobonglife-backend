package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.LoginSuccessCommand;

public interface HandleLoginSuccessUseCase {
    AuthTokens handle(LoginSuccessCommand command);
}
