package com.umust.dobonglife.commerce.client;

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

    public void canExchangeCoupon(Long userId) {
        restClient.post()
                .uri("/internal/users/{userId}/can-exchange", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<BaseResponse<Void>>() {});
    }

    public boolean isBlockedUser(Long userId) {
        BaseResponse<Boolean> response = restClient.get()
                .uri("/internal/users/{userId}/blocked", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return Boolean.TRUE.equals(response.getData());
    }

    public String getFcmToken(Long userId) {
        BaseResponse<String> response = restClient.get()
                .uri("/internal/users/{userId}/fcm-token", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return response.getData();
    }
}
