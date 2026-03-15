package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.HandleLoginSuccessUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.LoginSuccessCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginSuccessService implements HandleLoginSuccessUseCase {

    private final TokenIssuanceHelper tokenIssuanceHelper;
    private final AuthUserPort authUserPort;

    @Override
    public AuthTokens handle(LoginSuccessCommand command) {
        if (command.fcmToken() != null && !command.fcmToken().isBlank()) {
            authUserPort.updateFcmToken(command.userId(), command.fcmToken());
        }

        return tokenIssuanceHelper.issueAndStore(
                command.userId(), command.provider(), command.role(), command.userName()
        );
    }
}
