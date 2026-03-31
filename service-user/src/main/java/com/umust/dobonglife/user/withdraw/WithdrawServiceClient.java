package com.umust.dobonglife.user.withdraw;

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
public class WithdrawServiceClient {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;

    @Value("${services.commerce-service.url}")
    private String commerceServiceUrl;

    @Value("${services.content-service.url}")
    private String contentServiceUrl;

    // user-service internal calls go to self
    @Value("${server.port:8081}")
    private int selfPort;

    private RestClient userClient;
    private RestClient authClient;
    private RestClient commerceClient;
    private RestClient contentClient;

    private static final ParameterizedTypeReference<BaseResponse<Void>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(30_000);

        userClient = RestClient.builder().baseUrl("http://localhost:" + selfPort).requestFactory(factory).build();
        authClient = RestClient.builder().baseUrl(authServiceUrl).requestFactory(factory).build();
        commerceClient = RestClient.builder().baseUrl(commerceServiceUrl).requestFactory(factory).build();
        contentClient = RestClient.builder().baseUrl(contentServiceUrl).requestFactory(factory).build();
    }

    // User state (calls self - user-service)
    public void markPending(Long userId) { post(userClient, "/internal/users/{userId}/mark-pending", userId); }
    public void deleteAccount(Long userId) { post(userClient, "/internal/users/{userId}/delete", userId); }
    public void restoreAccount(Long userId) { post(userClient, "/internal/users/{userId}/restore", userId); }

    // Reviews & Likes (calls content-service)
    public void cleanupReviews(Long userId) { post(contentClient, "/internal/withdraw/reviews/{userId}/cleanup", userId); }
    public void cleanupLikes(Long userId) { post(contentClient, "/internal/withdraw/likes/{userId}/cleanup", userId); }
    public void restoreReviews(Long userId) { post(contentClient, "/internal/withdraw/reviews/{userId}/restore", userId); }
    public void restoreLikes(Long userId) { post(contentClient, "/internal/withdraw/likes/{userId}/restore", userId); }
    public void finalizeReviews(Long userId) { post(contentClient, "/internal/withdraw/reviews/{userId}/finalize", userId); }
    public void finalizeLikes(Long userId) { post(contentClient, "/internal/withdraw/likes/{userId}/finalize", userId); }

    // Coupons & Points (calls commerce-service)
    public void cleanupCoupons(Long userId) { post(commerceClient, "/internal/withdraw/coupons/{userId}/cleanup", userId); }
    public void cleanupPoints(Long userId) { post(commerceClient, "/internal/withdraw/points/{userId}/cleanup", userId); }
    public void restoreCoupons(Long userId) { post(commerceClient, "/internal/withdraw/coupons/{userId}/restore", userId); }
    public void restorePoints(Long userId) { post(commerceClient, "/internal/withdraw/points/{userId}/restore", userId); }
    public void finalizeCoupons(Long userId) { post(commerceClient, "/internal/withdraw/coupons/{userId}/finalize", userId); }
    public void finalizePoints(Long userId) { post(commerceClient, "/internal/withdraw/points/{userId}/finalize", userId); }

    // Social account revocation (calls auth-service)
    public void revokeSocialAccount(Long userId) { post(authClient, "/internal/auth/{userId}/revoke-social", userId); }

    // Token invalidation (calls auth-service)
    public void invalidateToken(String accessToken, String refreshToken) {
        try {
            authClient.post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/internal/auth/invalidate-token")
                                .queryParam("accessToken", accessToken);
                        if (refreshToken != null) builder.queryParam("refreshToken", refreshToken);
                        return builder.build();
                    })
                    .retrieve()
                    .body(RESPONSE_TYPE);
        } catch (Exception e) {
            log.error("[WithdrawServiceClient] 토큰 무효화 실패", e);
            throw new RuntimeException("토큰 무효화 HTTP 호출 실패", e);
        }
    }

    private void post(RestClient client, String uriTemplate, Long userId) {
        try {
            client.post().uri(uriTemplate, userId).retrieve().body(RESPONSE_TYPE);
        } catch (Exception e) {
            log.error("[WithdrawServiceClient] HTTP 호출 실패. uri={}, userId={}", uriTemplate, userId, e);
            throw new RuntimeException("HTTP 호출 실패: " + uriTemplate, e);
        }
    }
}
