package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.GoogleLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.IssueLoginTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.VerifyGoogleTokenUseCase;
import com.umust.dobonglife.domain.auth.domain.AuthTokens;
import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthUserUseCase;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleLoginService implements GoogleLoginUseCase {

    private final VerifyGoogleTokenUseCase verifyGoogleTokenUseCase;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final IssueLoginTokenUseCase issueLoginTokenUseCase;

    @Override
    @Transactional
    public AuthTokens login(String idToken, String fcmToken) {
        SocialUserInfo socialUser = verifyGoogleTokenUseCase.verify(idToken);

        User user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.GOOGLE, socialUser.providerId(), socialUser.email(), socialUser.name()
        );

        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(user.getId(), fcmToken);
        }

        return issueLoginTokenUseCase.issueLoginToken(user.getId(), Provider.GOOGLE, user.getRole(), user.getName());
    }
}
