package com.umust.dobonglife.global.client.auth;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.auth.out.AppleOAuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AppleAuthClient implements AppleOAuthPort {

    private final RestClient restClient;

    public AppleAuthClient(@Value("${service.auth.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "auth-service");
    }

    @Override
    public SocialAuthUserInfo verify(String identityToken) {
        return restClient.post()
                .uri("/internal/auth/oauth/apple/verify")
                .body(Map.of("token", identityToken))
                .retrieve()
                .body(SocialAuthUserInfo.class);
    }

    @Override
    public String exchangeAuthorizationCode(String authorizationCode) {
        return restClient.post()
                .uri("/internal/auth/oauth/apple/exchange")
                .body(Map.of("token", authorizationCode))
                .retrieve()
                .body(String.class);
    }

    @Override
    public void revokeToken(String refreshToken) {
        restClient.post()
                .uri("/internal/auth/oauth/apple/revoke")
                .body(Map.of("token", refreshToken))
                .retrieve()
                .toBodilessEntity();
    }
}
