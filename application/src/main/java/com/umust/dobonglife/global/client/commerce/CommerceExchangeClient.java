package com.umust.dobonglife.global.client.commerce;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.commerce.ExchangePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class CommerceExchangeClient implements ExchangePort {

    private final RestClient restClient;

    public CommerceExchangeClient(@Value("${service.commerce.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "commerce-service");
    }

    @Override
    public Map<String, Object> exchange(Long userId, Long promotionId) {
        return restClient.post()
                .uri("/internal/exchange?userId={userId}&promotionId={promotionId}", userId, promotionId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
