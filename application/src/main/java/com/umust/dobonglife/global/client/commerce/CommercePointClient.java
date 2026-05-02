package com.umust.dobonglife.global.client.commerce;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.commerce.PointPort;
import com.umust.dobonglife.global.port.dto.commerce.MyPointInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CommercePointClient implements PointPort {

    private final RestClient restClient;

    public CommercePointClient(@Value("${service.commerce.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "commerce-service");
    }

    @Override
    public Long getUserPoint(Long userId) {
        return restClient.get()
                .uri("/internal/point/balance/{userId}", userId)
                .retrieve()
                .body(Long.class);
    }

    @Override
    public MyPointInfo getMyPointHistory(Long userId, Long lastId, int size, String order) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/point/my")
                            .queryParam("userId", userId)
                            .queryParam("size", size)
                            .queryParam("order", order);
                    if (lastId != null) {
                        uriBuilder.queryParam("lastId", lastId);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
