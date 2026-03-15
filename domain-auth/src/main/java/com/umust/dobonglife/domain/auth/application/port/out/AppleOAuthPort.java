package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;

public interface AppleOAuthPort {
    SocialUserInfo verify(String identityToken);
    String exchangeAuthorizationCode(String authorizationCode);
    void revokeToken(String refreshToken);
}
