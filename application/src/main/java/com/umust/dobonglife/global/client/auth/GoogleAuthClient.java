package com.umust.dobonglife.global.client.auth;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.auth.out.GoogleOAuthPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Primary
@Component
public class GoogleAuthClient implements GoogleOAuthPort {

    private final RestClient restClient;

    public GoogleAuthClient(@Value("${service.auth.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "auth-service");
    }

    @Override
    public SocialAuthUserInfo verify(String idToken) {
        return restClient.post()
                .uri("/internal/auth/oauth/google/verify")
                .body(Map.of("token", idToken))
                .retrieve()
                .body(SocialAuthUserInfo.class);
    }

    @Override
    public void revokeToken(String accessToken) {
        restClient.post()
                .uri("/internal/auth/oauth/google/revoke")
                .body(Map.of("token", accessToken))
                .retrieve()
                .toBodilessEntity();
    }
}
