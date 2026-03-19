package com.umust.dobonglife.global.port.auth.out;

import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;

public interface KakaoOAuthPort {
    SocialAuthUserInfo verify(String accessToken);
    void unlinkUser(String providerId);
}
