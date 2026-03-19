package com.umust.dobonglife.application.auth.service;

import com.umust.dobonglife.global.port.auth.in.AuthTokenUseCase;
import com.umust.dobonglife.global.port.auth.in.RevokeSocialAccountUseCase;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.user.in.DeleteAccountUseCase;
import com.umust.dobonglife.global.port.user.UserPort;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthTokenUseCase authTokenUseCase;
    private final RevokeSocialAccountUseCase revokeSocialAccountUseCase;

    private final ManageUserUseCase manageUserUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final UserPort userPort;

    @Transactional
    public void logout(String accessToken, String refreshToken, Long userId) {
        manageUserUseCase.inValidFcmToken(userId);
        authTokenUseCase.logout(accessToken, refreshToken);
    }

    @Transactional
    public void deleteAccount(String accessToken, String refreshToken, Long userId) {
        log.info("=== [회원탈퇴 시작] userId: {}", userId);

        Provider provider = userPort.getProvider(userId);
        String providerId = userPort.getProviderId(userId);
        revokeSocialAccountUseCase.revoke(provider, providerId);

        // TODO: 각 도메인 모듈에 cleanup use case 추가 후 활성화
        // if (accountCleanupPort.isBusiness(userId)) {
        //     accountCleanupPort.cleanupBusinessData(userId);
        // }
        // accountCleanupPort.cleanupUserData(userId);
        deleteAccountUseCase.deleteAccount(userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                authTokenUseCase.invalidateAccessToken(accessToken);
                if (refreshToken != null) {
                    authTokenUseCase.deleteRefreshToken(refreshToken);
                }
            }
        });
    }

    public AuthTokens reissueTokens(String refreshToken) {
        return authTokenUseCase.reissueTokens(refreshToken);
    }
}
