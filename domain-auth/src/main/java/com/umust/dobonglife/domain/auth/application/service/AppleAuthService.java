package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.VerifyAppleTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AppleOAuthPort;
import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleAuthService implements VerifyAppleTokenUseCase {

    private final AppleOAuthPort appleOAuthPort;

    @Override
    public SocialUserInfo verify(String identityToken) {
        return appleOAuthPort.verify(identityToken);
    }

    @Override
    public String exchangeAuthorizationCode(String authorizationCode) {
        return appleOAuthPort.exchangeAuthorizationCode(authorizationCode);
    }
}
