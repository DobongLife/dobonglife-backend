package com.umust.dobonglife.global.client.user;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.port.BusinessPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BusinessClient implements BusinessPort {

    private final RestClient restClient;

    public BusinessClient(@Value("${service.user.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "user-service");
    }

    @Override
    public boolean checkBusiness(Long userId) {
        return Boolean.TRUE.equals(restClient.get()
                .uri("/internal/user/{userId}/business", userId)
                .retrieve()
                .body(Boolean.class));
    }

    @Override
    public Category getBusinessCategory(Long userId) {
        String category = restClient.get()
                .uri("/internal/user/{userId}/business/category", userId)
                .retrieve()
                .body(String.class);
        return Category.valueOf(category);
    }
}
