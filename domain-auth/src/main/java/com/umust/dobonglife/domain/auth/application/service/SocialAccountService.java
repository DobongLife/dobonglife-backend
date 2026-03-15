package com.umust.dobonglife.domain.auth.application.service;

import com.umust.dobonglife.domain.auth.application.port.in.RevokeSocialAccountUseCase;
import com.umust.dobonglife.domain.auth.application.port.out.AppleOAuthPort;
import com.umust.dobonglife.domain.auth.application.port.out.KakaoOAuthPort;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialAccountService implements RevokeSocialAccountUseCase {

    private final KakaoOAuthPort kakaoOAuthPort;
    private final AppleOAuthPort appleOAuthPort;

    @Override
    public void revoke(Provider provider, String providerIdOrToken) {
        try {
            if (provider == null || provider == Provider.LOCAL) {
                return;
            }
            switch (provider) {
                case KAKAO -> kakaoOAuthPort.unlinkUser(providerIdOrToken);
                case APPLE -> appleOAuthPort.revokeToken(providerIdOrToken);
                default -> log.warn("지원하지 않는 소셜 프로바이더: {}", provider);
            }
        } catch (Exception e) {
            log.warn("소셜 프로바이더 연결 해제 실패: provider={}, error={}", provider, e.getMessage());
        }
    }
}
