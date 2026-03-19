package com.umust.dobonglife.global.client.content;

import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.port.content.LikePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ContentLikeClient implements LikePort {

    private final RestClient restClient;

    public ContentLikeClient(@Value("${service.content.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public boolean toggleLike(Long userId, TargetType targetType, Long targetId) {
        return Boolean.TRUE.equals(restClient.post()
                .uri("/internal/like/{targetType}/{targetId}?userId={userId}",
                        targetType.name(), targetId, userId)
                .retrieve()
                .body(Boolean.class));
    }

    @Override
    public Map<String, Object> getMyLikedPlaces(Long userId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/like/place/my")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getMyLikedCourses(Long userId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/like/course/my")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
