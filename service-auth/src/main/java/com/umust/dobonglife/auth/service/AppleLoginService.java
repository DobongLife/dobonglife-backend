package com.umust.dobonglife.auth.service;

import com.umust.dobonglife.auth.client.UserServiceClient;
import com.umust.dobonglife.auth.client.UserServiceClient.OAuthUserResponse;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.SocialAuthUserInfo;
import com.umust.dobonglife.domain.auth.application.port.in.LoginSuccessUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AppleOAuthPort;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleLoginService {

    private final AppleOAuthPort appleOAuthPort;
    private final UserServiceClient userServiceClient;
    private final LoginSuccessUseCase loginSuccessUseCase;

    public AuthTokens login(String identityToken, String fcmToken, String providerToken) {
        SocialAuthUserInfo socialUser = appleOAuthPort.verify(identityToken);

        OAuthUserResponse user = userServiceClient.findOrCreateOAuthUser(
                Provider.APPLE.name(), socialUser.providerId(), socialUser.email(), socialUser.name()
        );

        if (fcmToken != null && !fcmToken.isBlank()) {
            userServiceClient.updateFcmToken(user.id(), fcmToken);
        }

        return loginSuccessUseCase.issueLoginToken(user.id(), Provider.APPLE, Role.valueOf(user.role()), user.name());
    }
}
