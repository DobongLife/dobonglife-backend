package com.umust.dobonglife.application.withdraw.client;

import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class WithdrawRestClient {

    @Value("${withdraw.base-url}")
    private String baseUrl;

    private RestClient restClient;

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 30_000;
    private static final ParameterizedTypeReference<BaseResponse<Void>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl + "/internal/withdraw")
                .requestFactory(factory)
                .build();
    }

    // ── User 상태 변경 ──

    public void markPending(Long userId) {
        post("/users/{userId}/mark-pending", userId);
    }

    public void deleteAccount(Long userId) {
        post("/users/{userId}/delete", userId);
    }

    public void restoreAccount(Long userId) {
        post("/users/{userId}/restore", userId);
    }

    // ── 종속 데이터 정리 ──

    public void cleanupReviews(Long userId) {
        post("/reviews/{userId}/cleanup", userId);
    }

    public void restoreReviews(Long userId) {
        post("/reviews/{userId}/restore", userId);
    }

    public void cleanupLikes(Long userId) {
        post("/likes/{userId}/cleanup", userId);
    }

    public void restoreLikes(Long userId) {
        post("/likes/{userId}/restore", userId);
    }

    public void cleanupCoupons(Long userId) {
        post("/coupons/{userId}/cleanup", userId);
    }

    public void restoreCoupons(Long userId) {
        post("/coupons/{userId}/restore", userId);
    }

    public void cleanupPoints(Long userId) {
        post("/points/{userId}/cleanup", userId);
    }

    public void restorePoints(Long userId) {
        post("/points/{userId}/restore", userId);
    }

    // ── 소셜 연동 해제 ──

    public void revokeSocialAccount(Long userId) {
        post("/auth/{userId}/revoke-social", userId);
    }

    // ── 토큰 무효화 ──

    public void invalidateToken(String accessToken, String refreshToken) {
        try {
            BaseResponse<Void> response = restClient.post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/auth/invalidate-token")
                                .queryParam("accessToken", accessToken);
                        if (refreshToken != null) {
                            builder.queryParam("refreshToken", refreshToken);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .body(RESPONSE_TYPE);
            validateResponse(response, "/auth/invalidate-token");
        } catch (RestClientResponseException e) {
            log.error("[WithdrawRestClient] 토큰 무효화 실패. status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("토큰 무효화 HTTP 호출 실패", e);
        }
    }

    private void post(String uriTemplate, Long userId) {
        try {
            BaseResponse<Void> response = restClient.post()
                    .uri(uriTemplate, userId)
                    .retrieve()
                    .body(RESPONSE_TYPE);
            validateResponse(response, uriTemplate);
        } catch (RestClientResponseException e) {
            log.error("[WithdrawRestClient] HTTP 호출 실패. uri={}, userId={}, status={}, body={}",
                    uriTemplate, userId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("회원탈퇴 HTTP 호출 실패: " + uriTemplate, e);
        }
    }

    private void validateResponse(BaseResponse<Void> response, String uri) {
        if (response == null || !response.isSuccess()) {
            String message = response != null ? response.getMessage() : "응답 없음";
            log.error("[WithdrawRestClient] 응답 실패. uri={}, message={}", uri, message);
            throw new RuntimeException("회원탈퇴 HTTP 응답 실패: " + uri + " - " + message);
        }
    }
}
