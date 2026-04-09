package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.SocialAuthUserInfo;

public interface KakaoOAuthPort {
    SocialAuthUserInfo verify(String accessToken);
    void unlinkUser(String providerId);
}
