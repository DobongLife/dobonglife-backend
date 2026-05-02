package com.umust.dobonglife.global.client.content;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.port.content.ReviewPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ContentReviewClient implements ReviewPort {

    private final RestClient restClient;

    public ContentReviewClient(@Value("${service.content.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "content-service");
    }

    @Override
    public Map<String, Object> createReview(Long userId, Map<String, Object> request) {
        return restClient.post()
                .uri("/internal/review?userId={userId}", userId)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> updateReview(Long reviewId, Long userId, Map<String, Object> request) {
        return restClient.patch()
                .uri("/internal/review/{reviewId}?userId={userId}", reviewId, userId)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        restClient.delete()
                .uri("/internal/review/{reviewId}?userId={userId}", reviewId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Map<String, Object> getReviews(TargetType targetType, Long targetId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/review/{targetType}/{targetId}")
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build(targetType.name(), targetId);
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> getMyReviews(Long userId, TargetType targetType, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/review/my/{targetType}")
                            .queryParam("userId", userId)
                            .queryParam("size", size);
                    if (lastId != null) uriBuilder.queryParam("lastId", lastId);
                    return uriBuilder.build(targetType.name());
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
