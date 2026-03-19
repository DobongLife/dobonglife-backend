package com.umust.dobonglife.global.client.content;

import com.umust.dobonglife.global.port.content.PlacePort;
import com.umust.dobonglife.global.port.dto.content.PlaceSummaryInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ContentPlaceClient implements PlacePort {

    private final RestClient restClient;

    public ContentPlaceClient(@Value("${service.content.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public List<PlaceSummaryInfo> getAllActivePlaces(Long userId) {
        return restClient.get()
                .uri("/internal/place/active?userId={userId}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getPlaceDetail(Long placeId, Long userId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/place/{placeId}/detail")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build(placeId);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
