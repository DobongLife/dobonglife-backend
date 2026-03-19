package com.umust.dobonglife.global.port.auth.out;

import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;

public interface AppleOAuthPort {
    SocialAuthUserInfo verify(String identityToken);
    String exchangeAuthorizationCode(String authorizationCode);
    void revokeToken(String refreshToken);
}
