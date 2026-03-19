package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.SocialAuthUserInfo;

public interface GoogleOAuthPort {
    SocialAuthUserInfo verify(String idToken);
    void revokeToken(String accessToken);
}
