package com.umust.dobonglife.global.client.user;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.port.user.UserPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient implements UserPort {

    private final RestClient restClient;

    public UserClient(@Value("${service.user.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "user-service");
    }

    @Override
    public boolean isBlockedUser(Long userId) {
        return Boolean.TRUE.equals(restClient.get()
                .uri("/internal/user/{userId}/blocked", userId)
                .retrieve()
                .body(Boolean.class));
    }

    @Override
    public String getFcmToken(Long userId) {
        return restClient.get()
                .uri("/internal/user/{userId}/fcm-token", userId)
                .retrieve()
                .body(String.class);
    }

    @Override
    public Provider getProvider(Long userId) {
        String provider = restClient.get()
                .uri("/internal/user/{userId}/provider", userId)
                .retrieve()
                .body(String.class);
        return Provider.valueOf(provider);
    }

    @Override
    public String getProviderId(Long userId) {
        return restClient.get()
                .uri("/internal/user/{userId}/provider-id", userId)
                .retrieve()
                .body(String.class);
    }

    @Override
    public String getProviderToken(Long userId) {
        return restClient.get()
                .uri("/internal/user/{userId}/provider-token", userId)
                .retrieve()
                .body(String.class);
    }
}
