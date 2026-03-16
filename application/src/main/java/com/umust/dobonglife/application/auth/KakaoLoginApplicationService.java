package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.KakaoLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.LoginSuccessUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.KakaoOAuthPort;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.SocialAuthUserInfo;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.domain.user.application.port.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoLoginApplicationService implements KakaoLoginUseCase {

    private final KakaoOAuthPort kakaoOAuthPort;
    private final OAuthFindUserUseCase oAuthFindUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final LoginSuccessUseCase loginSuccessUseCase;

    @Override
    @Transactional
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
