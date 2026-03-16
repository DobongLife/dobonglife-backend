package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.GoogleLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.IssueTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.GoogleOAuthPort;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.domain.user.application.port.in.OAuthUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleLoginApplicationService implements GoogleLoginUseCase {

    private final GoogleOAuthPort googleOAuthPort;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final IssueTokenUseCase issueTokenUseCase;

    @Override
    @Transactional
    public AuthTokens login(String idToken, String fcmToken) {
        SocialUserInfo socialUser = googleOAuthPort.verify(idToken);

        OAuthLoginUser user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.GOOGLE, socialUser.providerId(), socialUser.email(), socialUser.name()
        );

        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(user.id(), fcmToken);
        }

        return issueTokenUseCase.issueLoginToken(user.id(), Provider.GOOGLE, user.role(), user.name());
    }
}
