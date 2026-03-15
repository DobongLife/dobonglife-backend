package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.AccountCleanupPort;
import com.umust.dobonglife.domain.auth.application.port.in.ExtractTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.InvalidateTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.LogoutUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.RevokeSocialAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.DeleteAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import jakarta.servlet.http.HttpServletRequest;
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

    private final ExtractTokenUseCase extractTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final InvalidateTokenUseCase invalidateTokenUseCase;
    private final RevokeSocialAccountUseCase revokeSocialAccountUseCase;

    private final ManageUserUseCase manageUserUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetUserUseCase getUserUseCase;

    private final AccountCleanupPort accountCleanupPort;

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = extractTokenUseCase.extractAccessToken(request);
        String refreshToken = extractTokenUseCase.extractRefreshToken(request)
                .orElseThrow(() -> new com.umust.dobonglife.global.error.exception.BusinessException(
                        com.umust.dobonglife.global.error.DomainErrorCode.REFRESH_TOKEN_NOT_FOUND));

        Long userId = extractTokenUseCase.getUserId(accessToken);

        manageUserUseCase.inValidFcmToken(userId);
        logoutUseCase.logout(accessToken, refreshToken);
    }

    @Transactional
    public void deleteAccount(HttpServletRequest request, Long userId) {
        log.info("=== [회원탈퇴 시작] userId: {}", userId);

        Provider provider = getUserUseCase.getProvider(userId);
        String providerId = getUserUseCase.getProviderId(userId);
        revokeSocialAccountUseCase.revoke(provider, providerId);

        String accessToken = extractTokenUseCase.extractAccessToken(request);
        String refreshToken = extractTokenUseCase.extractRefreshToken(request).orElse(null);

        if (accountCleanupPort.isBusiness(userId)) {
            accountCleanupPort.cleanupBusinessData(userId);
        }
        accountCleanupPort.cleanupUserData(userId);
        deleteAccountUseCase.deleteAccount(userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                invalidateTokenUseCase.invalidateAccessToken(accessToken);
                if (refreshToken != null) {
                    invalidateTokenUseCase.deleteRefreshToken(refreshToken);
                }
            }
        });
    }
}
