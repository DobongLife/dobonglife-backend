package com.umust.dobonglife.global.port.auth.out;

import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;

public interface GoogleOAuthPort {
    SocialAuthUserInfo verify(String idToken);
    void revokeToken(String accessToken);
}
