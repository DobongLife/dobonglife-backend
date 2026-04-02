package com.umust.dobonglife.application.auth.service;

import com.umust.dobonglife.application.withdraw.WithdrawOrchestrator;
import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.exception.UserErrorCode;
import com.umust.dobonglife.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthTokenUseCase authTokenUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final WithdrawOrchestrator withdrawOrchestrator;

    public void logout(String accessToken, String refreshToken, Long userId) {
        manageUserUseCase.inValidFcmToken(userId);
        authTokenUseCase.logout(accessToken, refreshToken);
    }

    public void deleteAccount(String accessToken, String refreshToken, Long userId) {
        withdrawOrchestrator.execute(userId, accessToken, refreshToken);
    }

    public AuthTokens reissueTokens(String refreshToken) {
        Long userId = authTokenUseCase.extractUserId(refreshToken);

        if (getUserUseCase.isNotActiveUser(userId)) {
            throw new UserException(UserErrorCode.USER_ALREADY_WITHDRAWN);
        }

        return authTokenUseCase.reissueTokens(refreshToken);
    }
}
