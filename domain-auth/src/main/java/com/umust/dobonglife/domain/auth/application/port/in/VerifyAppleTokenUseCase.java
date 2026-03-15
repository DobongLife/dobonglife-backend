package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.domain.SocialUserInfo;

public interface VerifyAppleTokenUseCase {
    SocialUserInfo verify(String identityToken);
    String exchangeAuthorizationCode(String authorizationCode);
}
