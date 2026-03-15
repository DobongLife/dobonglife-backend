package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.AppleLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.IssueLoginTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.VerifyAppleTokenUseCase;
import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;
import com.umust.dobonglife.domain.auth.dto.request.AppleLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthUserUseCase;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleLoginService implements AppleLoginUseCase {

    private final VerifyAppleTokenUseCase verifyAppleTokenUseCase;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final IssueLoginTokenUseCase issueLoginTokenUseCase;

    @Override
    @Transactional
    public TokenResponse login(AppleLoginRequest request) {
        SocialUserInfo socialUser = verifyAppleTokenUseCase.verify(request.getIdentityToken());

        User user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.APPLE, socialUser.providerId(), socialUser.email(), socialUser.email()
        );

        updateFcmTokenIfPresent(user.getId(), request.getFcmToken());
        exchangeProviderTokenIfPresent(user.getId(), request.getProviderToken());

        return issueLoginTokenUseCase.issueLoginToken(user.getId(), Provider.APPLE, user.getRole(), user.getName());
    }

    private void updateFcmTokenIfPresent(Long userId, String fcmToken) {
        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(userId, fcmToken);
        }
    }

    private void exchangeProviderTokenIfPresent(Long userId, String providerToken) {
        if (providerToken != null && !providerToken.isBlank()) {
            String refreshToken = verifyAppleTokenUseCase.exchangeAuthorizationCode(providerToken);
            manageUserUseCase.updateProviderToken(userId, refreshToken);
        } else {
            log.warn("[Apple Login] providerToken(authorizationCode)이 요청에 없음: userId={}", userId);
        }
    }
}
