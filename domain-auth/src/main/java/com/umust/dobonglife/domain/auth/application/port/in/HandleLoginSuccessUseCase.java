package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.domain.AuthTokens;
import com.umust.dobonglife.domain.auth.domain.LoginSuccessCommand;

public interface HandleLoginSuccessUseCase {
    AuthTokens handle(LoginSuccessCommand command);
}
