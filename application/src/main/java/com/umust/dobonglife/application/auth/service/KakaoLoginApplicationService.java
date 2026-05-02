package com.umust.dobonglife.application.auth.service;

import com.umust.dobonglife.application.auth.port.in.KakaoLoginUseCase;
import com.umust.dobonglife.global.port.auth.in.LoginSuccessUseCase;
import com.umust.dobonglife.global.port.auth.out.KakaoOAuthPort;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
import com.umust.dobonglife.global.port.user.dto.OAuthLoginUser;
import com.umust.dobonglife.global.port.user.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoLoginApplicationService implements KakaoLoginUseCase {

    private final KakaoOAuthPort kakaoOAuthPort;
    private final OAuthFindUserUseCase oAuthFindUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final LoginSuccessUseCase loginSuccessUseCase;

    @Override
    public AuthTokens login(String accessToken, String fcmToken) {
        SocialAuthUserInfo socialUser = kakaoOAuthPort.verify(accessToken);

        OAuthLoginUser user = oAuthFindUserUseCase.findOrCreateOAuthUser(
                Provider.KAKAO, socialUser.providerId(), socialUser.email(), socialUser.name()
        );

        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(user.id(), fcmToken);
        }

        return loginSuccessUseCase.issueLoginToken(user.id(), Provider.KAKAO, user.role(), user.name());
    }
}
