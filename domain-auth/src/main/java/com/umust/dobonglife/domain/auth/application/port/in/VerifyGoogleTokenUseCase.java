package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;

public interface VerifyGoogleTokenUseCase {
    SocialUserInfo verify(String idToken);
}
