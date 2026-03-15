package com.umust.dobonglife.application.auth;

import com.umust.dobonglife.application.auth.port.in.KakaoLoginUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.IssueLoginTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.VerifyKakaoTokenUseCase;
import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;
import com.umust.dobonglife.domain.auth.dto.request.KakaoLoginRequest;
import com.umust.dobonglife.domain.auth.dto.response.TokenResponse;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthUserUseCase;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService implements KakaoLoginUseCase {

    private final VerifyKakaoTokenUseCase verifyKakaoTokenUseCase;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final IssueLoginTokenUseCase issueLoginTokenUseCase;

    @Override
    @Transactional
    public TokenResponse login(KakaoLoginRequest request) {
        SocialUserInfo socialUser = verifyKakaoTokenUseCase.verify(request.getAccessToken());

        User user = oAuthUserUseCase.findOrCreateOAuthUser(
                Provider.KAKAO, socialUser.providerId(), socialUser.email(), socialUser.name()
        );

        updateFcmTokenIfPresent(user.getId(), request.getFcmToken());

        return issueLoginTokenUseCase.issueLoginToken(user.getId(), Provider.KAKAO, user.getRole(), user.getName());
    }

    private void updateFcmTokenIfPresent(Long userId, String fcmToken) {
        if (fcmToken != null && !fcmToken.isBlank()) {
            manageUserUseCase.updateFcmToken(userId, fcmToken);
        }
    }
}
