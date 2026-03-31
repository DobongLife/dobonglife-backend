package com.umust.dobonglife.auth.client;

import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class UserServiceClient {

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    private RestClient restClient;

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(10_000);
        restClient = RestClient.builder().baseUrl(userServiceUrl).requestFactory(factory).build();
    }

    public OAuthUserResponse findOrCreateOAuthUser(String provider, String providerId, String email, String name) {
        OAuthUserRequest request = new OAuthUserRequest(provider, providerId, email, name);
        BaseResponse<OAuthUserResponse> response = restClient.post()
                .uri("/internal/users/oauth/find-or-create")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response.getData();
    }

    public void updateFcmToken(Long userId, String fcmToken) {
        restClient.post()
                .uri("/internal/users/{userId}/fcm-token?fcmToken={fcmToken}", userId, fcmToken)
                .retrieve()
                .body(new ParameterizedTypeReference<BaseResponse<Void>>() {});
    }

    public void invalidateFcmToken(Long userId) {
        restClient.post()
                .uri("/internal/users/{userId}/fcm-token/invalidate", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<BaseResponse<Void>>() {});
    }

    public boolean isActiveUser(Long userId) {
        BaseResponse<Boolean> response = restClient.get()
                .uri("/internal/users/{userId}/active", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return Boolean.TRUE.equals(response.getData());
    }

    public UserProviderResponse getUserProvider(Long userId) {
        BaseResponse<UserProviderResponse> response = restClient.get()
                .uri("/internal/users/{userId}/provider", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response.getData();
    }

    public record OAuthUserRequest(String provider, String providerId, String email, String name) {}
    public record OAuthUserResponse(Long id, String role, String name) {}
    public record UserProviderResponse(String provider, String providerId) {}
}
