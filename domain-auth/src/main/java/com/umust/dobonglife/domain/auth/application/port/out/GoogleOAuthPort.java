package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;

public interface GoogleOAuthPort {
    SocialUserInfo verify(String idToken);
    void revokeToken(String accessToken);
}
