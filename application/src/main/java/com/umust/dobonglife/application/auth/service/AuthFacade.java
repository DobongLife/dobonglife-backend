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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthTokenUseCase authTokenUseCase;
    private final RevokeSocialAccountUseCase revokeSocialAccountUseCase;

    private final ManageUserUseCase manageUserUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final UserPort userPort;

    public void logout(String accessToken, String refreshToken, Long userId) {
        manageUserUseCase.inValidFcmToken(userId);
        authTokenUseCase.logout(accessToken, refreshToken);
    }

    public void deleteAccount(String accessToken, String refreshToken, Long userId) {
        log.info("=== [회원탈퇴 시작] userId: {}", userId);

        // 1. 되돌릴 수 있는 작업 먼저 (soft delete)
        deleteAccountUseCase.deleteAccount(userId);

        // 2. 되돌릴 수 없는 작업 — 실패해도 계정 삭제는 유지
        try {
            Provider provider = userPort.getProvider(userId);
            revokeSocialAccountUseCase.revoke(provider, resolveProviderCredential(provider, userId));
        } catch (Exception e) {
            log.warn("[회원탈퇴] 소셜 연동 해제 실패 — 별도 배치에서 처리: userId={}", userId, e);
        }

        // 3. 토큰 무효화 (best-effort)
        try {
            authTokenUseCase.invalidateAccessToken(accessToken);
            if (refreshToken != null) {
                authTokenUseCase.deleteRefreshToken(refreshToken);
            }
        } catch (Exception e) {
            log.warn("[회원탈퇴] 토큰 무효화 실패 — TTL 만료 대기: userId={}", userId, e);
        }

        log.info("=== [회원탈퇴 완료] userId: {}", userId);
    }

    public AuthTokens reissueTokens(String refreshToken) {
        return authTokenUseCase.reissueTokens(refreshToken);
    }

    private String resolveProviderCredential(Provider provider, Long userId) {
        if (provider == Provider.APPLE) {
            return userPort.getProviderToken(userId);
        }
        return userPort.getProviderId(userId);
    }
}
