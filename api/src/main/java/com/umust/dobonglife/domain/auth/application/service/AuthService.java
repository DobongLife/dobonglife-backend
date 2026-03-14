package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.AccountCleanupPort;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.umust.dobonglife.domain.user.application.port.in.DeleteAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
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
public class AuthService {

    private final JwtService jwtService;
    private final ManageUserUseCase manageUserUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetUserUseCase getUserUseCase;
    private final JwtTokenProvider jwtUtil;
    private final KakaoAuthService kakaoAuthService;
    private final GoogleAuthService googleAuthService;
    private final AppleAuthService appleAuthService;
    private final AccountCleanupPort accountCleanupPort;

    @Transactional
    public void logout(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        log.info("LogOut Access Token: {}", accessToken);

        jwtUtil.validateToken(accessToken);

        String refreshToken = jwtUtil.extractRefreshToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        jwtUtil.validateToken(refreshToken);
        if (!"refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            throw new CustomJwtException(ErrorCode.INVALID_REFRESH_TYPE);
        }

        Long userId = jwtUtil.getUserId(accessToken);

        manageUserUseCase.inValidFcmToken(userId);

        jwtService.deleteRefreshToken(refreshToken);
        jwtService.invalidAccessToken(accessToken);
    }

    @Transactional
    public void deleteAccount(HttpServletRequest request, Long userId) {
        log.info("=== [회원탈퇴 시작] userId: {}", userId);

        revokeProviderAccount(userId);

        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));

        String refreshToken = jwtUtil.extractRefreshToken(request).orElse(null);

        if (accountCleanupPort.isBusiness(userId)) {
            accountCleanupPort.cleanupBusinessData(userId);
        }

        accountCleanupPort.cleanupUserData(userId);
        deleteAccountUseCase.deleteAccount(userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jwtService.invalidAccessToken(accessToken);
                if (refreshToken != null) {
                    jwtService.deleteRefreshToken(refreshToken);
                }
            }
        });
    }

    private void revokeProviderAccount(Long userId) {
        try {
            User user = getUserUseCase.findById(userId);
            Provider provider = user.getProvider();
            if (provider == null || provider == Provider.LOCAL) {
                return;
            }
            switch (provider) {
                case KAKAO -> kakaoAuthService.unlinkUser(user.getProviderId());
                case APPLE -> appleAuthService.revokeToken(user.getProviderToken());
                default -> log.warn("지원하지 않는 소셜 프로바이더: {}", provider);
            }
        } catch (Exception e) {
            log.warn("소셜 프로바이더 연결 해제 실패 (계정 삭제는 계속 진행): userId={}, error={}", userId, e.getMessage());
        }
    }
}
