package com.umust.dobonglife.global.client.commerce;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.commerce.CouponPort;
import com.umust.dobonglife.global.port.dto.commerce.MyCouponInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CommerceCouponClient implements CouponPort {

    private final RestClient restClient;

    public CommerceCouponClient(@Value("${service.commerce.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "commerce-service");
    }

    @Override
    public MyCouponInfo getMyCoupons(Long userId, Long lastId, int size) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/internal/coupon/my")
                            .queryParam("userId", userId)
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
    public void useCoupon(Long promotionId, String code, Long couponId, Long userId) {
        restClient.post()
                .uri("/internal/coupon/use/{couponId}?userId={userId}&promotionId={promotionId}&code={code}",
                        couponId, userId, promotionId, code)
                .retrieve()
                .toBodilessEntity();
    }
}
