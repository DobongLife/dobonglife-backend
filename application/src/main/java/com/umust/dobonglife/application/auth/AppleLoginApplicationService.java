package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.AppleLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.LoginSuccessUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AppleOAuthPort;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.SocialAuthUserInfo;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.domain.user.application.port.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleLoginApplicationService implements AppleLoginUseCase {

    private final AppleOAuthPort appleOAuthPort;
    private final OAuthFindUserUseCase oAuthFindUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final LoginSuccessUseCase loginSuccessUseCase;

    @Override
    @Transactional
    public AuthTokens login(String identityToken, String fcmToken, String providerToken) {
        SocialAuthUserInfo socialUser = appleOAuthPort.verify(identityToken);

        OAuthLoginUser user = oAuthFindUserUseCase.findOrCreateOAuthUser(
                Provider.APPLE, socialUser.providerId(), socialUser.email(), socialUser.email()
        );

        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(user.id(), fcmToken);
        }

        if (providerToken != null && !providerToken.isBlank()) {
            String refreshToken = appleOAuthPort.exchangeAuthorizationCode(providerToken);
            manageUserUseCase.updateProviderToken(user.id(), refreshToken);
        } else {
            log.warn("[Apple Login] providerToken(authorizationCode)이 요청에 없음: userId={}", user.id());
        }

        return loginSuccessUseCase.issueLoginToken(user.id(), Provider.APPLE, user.role(), user.name());
    }
}
