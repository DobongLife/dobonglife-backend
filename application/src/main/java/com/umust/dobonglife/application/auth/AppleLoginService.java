package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.AppleLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.IssueLoginTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.VerifyAppleTokenUseCase;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;
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
    public AuthTokens login(String identityToken, String fcmToken, String providerToken) {
        SocialUserInfo socialUser = verifyAppleTokenUseCase.verify(identityToken);

        User user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.APPLE, socialUser.providerId(), socialUser.email(), socialUser.email()
        );

        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(user.getId(), fcmToken);
        }

        if (providerToken != null && !providerToken.isBlank()) {
            String refreshToken = verifyAppleTokenUseCase.exchangeAuthorizationCode(providerToken);
            manageUserUseCase.updateProviderToken(user.getId(), refreshToken);
        } else {
            log.warn("[Apple Login] providerToken(authorizationCode)이 요청에 없음: userId={}", user.getId());
        }

        return issueLoginTokenUseCase.issueLoginToken(user.getId(), Provider.APPLE, user.getRole(), user.getName());
    }
}
