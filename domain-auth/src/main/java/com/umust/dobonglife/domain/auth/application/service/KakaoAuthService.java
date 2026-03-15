package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.VerifyKakaoTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.KakaoOAuthPort;
import com.umust.dobonglife.domain.auth.application.dto.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAuthService implements VerifyKakaoTokenUseCase {

    private final KakaoOAuthPort kakaoOAuthPort;

    @Override
    public SocialUserInfo verify(String accessToken) {
        return kakaoOAuthPort.verify(accessToken);
    }
}
