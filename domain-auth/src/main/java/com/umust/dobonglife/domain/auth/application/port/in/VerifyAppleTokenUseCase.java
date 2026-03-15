package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;

public interface VerifyAppleTokenUseCase {
    SocialUserInfo verify(String identityToken);
    String exchangeAuthorizationCode(String authorizationCode);
}
