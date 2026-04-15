package com.umust.dobonglife.global.client.auth;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.auth.out.KakaoOAuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class KakaoAuthClient implements KakaoOAuthPort {

    private final RestClient restClient;

    public KakaoAuthClient(@Value("${service.auth.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "auth-service");
    }

    @Override
    public SocialAuthUserInfo verify(String accessToken) {
        return restClient.post()
                .uri("/internal/auth/oauth/kakao/verify")
                .body(Map.of("token", accessToken))
                .retrieve()
                .body(SocialAuthUserInfo.class);
    }

    @Override
    public void unlinkUser(String providerId) {
        restClient.post()
                .uri("/internal/auth/oauth/kakao/unlink")
                .body(Map.of("token", providerId))
                .retrieve()
                .toBodilessEntity();
    }
}
