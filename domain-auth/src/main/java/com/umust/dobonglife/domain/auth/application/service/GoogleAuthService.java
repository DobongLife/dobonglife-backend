package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.VerifyGoogleTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.GoogleOAuthPort;
import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthService implements VerifyGoogleTokenUseCase {

    private final GoogleOAuthPort googleOAuthPort;

    @Override
    public SocialUserInfo verify(String idTokenString) {
        return googleOAuthPort.verify(idTokenString);
    }
}
