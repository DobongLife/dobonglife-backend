package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;

public interface VerifyKakaoTokenUseCase {
    SocialUserInfo verify(String accessToken);
}
