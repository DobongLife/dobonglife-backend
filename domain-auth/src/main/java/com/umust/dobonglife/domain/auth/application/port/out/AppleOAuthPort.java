package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.SocialAuthUserInfo;

public interface AppleOAuthPort {
    SocialAuthUserInfo verify(String identityToken);
    String exchangeAuthorizationCode(String authorizationCode);
    void revokeToken(String refreshToken);
}
