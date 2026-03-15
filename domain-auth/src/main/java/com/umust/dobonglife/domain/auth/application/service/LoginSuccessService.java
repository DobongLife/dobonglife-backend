package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.HandleLoginSuccessUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.domain.auth.domain.AuthTokens;
import com.umust.dobonglife.domain.auth.domain.LoginSuccessCommand;
import com.umust.dobonglife.domain.auth.infrastructure.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginSuccessService implements HandleLoginSuccessUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtService jwtService;
    private final AuthUserPort authUserPort;

    @Override
    public AuthTokens handle(LoginSuccessCommand command) {
        if (command.fcmToken() != null && !command.fcmToken().isBlank()) {
            authUserPort.updateFcmToken(command.userId(), command.fcmToken());
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                command.userId(), command.provider(), command.role(), command.userName()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(
                command.userId(), command.provider(), command.role(), command.userName()
        );

        jwtService.storeRefreshToken(refreshToken, command.userId());

        return new AuthTokens(accessToken, refreshToken, command.role());
    }
}
