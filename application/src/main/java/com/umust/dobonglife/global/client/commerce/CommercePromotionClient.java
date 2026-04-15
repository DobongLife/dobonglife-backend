package com.umust.dobonglife.global.client.commerce;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionPresetInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionRegisterInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionUpdateInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class CommercePromotionClient implements PromotionPort {

    private final RestClient restClient;

    public CommercePromotionClient(@Value("${service.commerce.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "commerce-service");
    }

    @Override
    public CursorResponse<PromotionAdInfo> getAdPromotions(Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/promotion/ad")
                            .queryParam("size", size);
                    if (lastId != null) {
                        uriBuilder.queryParam("lastId", lastId);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public CursorResponse<PromotionSummaryInfo> getPromotions(Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/promotion")
                            .queryParam("size", size);
                    if (lastId != null) {
                        uriBuilder.queryParam("lastId", lastId);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public PromotionRegisterInfo registerPromotion(Map<String, Object> request, Long userId, List<String> imageUrls) {
        Map<String, Object> body = new java.util.HashMap<>(request);
        body.put("userId", userId);
        body.put("imageUrls", imageUrls);

        return restClient.post()
                .uri("/internal/promotion/register")
                .body(body)
                .retrieve()
                .body(PromotionRegisterInfo.class);
    }

    @Override
    public PromotionUpdateInfo updatePromotion(String title, String description, Long totalQuantity, Long promotionId, Long userId) {
        Map<String, Object> body = Map.of(
                "title", title,
                "description", description,
                "totalQuantity", totalQuantity,
                "userId", userId
        );

        return restClient.patch()
                .uri("/internal/promotion/{promotionId}", promotionId)
                .body(body)
                .retrieve()
                .body(PromotionUpdateInfo.class);
    }

    @Override
    public PromotionPresetInfo getPresetByCategory(String category) {
        return restClient.get()
                .uri("/internal/promotion/preset?category={category}", category)
                .retrieve()
                .body(PromotionPresetInfo.class);
    }
}
