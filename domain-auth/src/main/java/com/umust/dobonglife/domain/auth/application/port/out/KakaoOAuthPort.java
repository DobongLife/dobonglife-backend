package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;

public interface KakaoOAuthPort {
    SocialUserInfo verify(String accessToken);
    void unlinkUser(String providerId);
}
