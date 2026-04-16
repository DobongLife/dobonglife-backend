package com.umust.dobonglife.application.withdraw.client;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class WithdrawRestClient {

    private final RestClient userClient;
    private final RestClient contentClient;
    private final RestClient commerceClient;
    private final RestClient authClient;

    public WithdrawRestClient(
            @Value("${service.user.url}") String userUrl,
            @Value("${service.content.url}") String contentUrl,
            @Value("${service.commerce.url}") String commerceUrl,
            @Value("${service.auth.url}") String authUrl) {
        this.userClient = InternalRestClientFactory.create(userUrl, "user-service");
        this.contentClient = InternalRestClientFactory.create(contentUrl, "content-service");
        this.commerceClient = InternalRestClientFactory.create(commerceUrl, "commerce-service");
        this.authClient = InternalRestClientFactory.create(authUrl, "auth-service");
    }

    // ── User 상태 변경 ──

    public void markPending(Long userId) {
        userClient.post()
                .uri("/internal/user/{userId}/mark-pending", userId)
                .retrieve().toBodilessEntity();
    }

    public void deleteAccount(Long userId) {
        userClient.delete()
                .uri("/internal/user/{userId}", userId)
                .retrieve().toBodilessEntity();
    }

    public void restoreAccount(Long userId) {
        userClient.post()
                .uri("/internal/user/{userId}/restore", userId)
                .retrieve().toBodilessEntity();
    }

    // ── 종속 데이터 정리 (ACTIVE → PENDING) ──

    public void cleanupReviews(Long userId) {
        contentClient.post()
                .uri("/internal/review/withdraw/{userId}/cleanup", userId)
                .retrieve().toBodilessEntity();
    }

    public void cleanupLikes(Long userId) {
        contentClient.post()
                .uri("/internal/like/withdraw/{userId}/cleanup", userId)
                .retrieve().toBodilessEntity();
    }

    public void cleanupCoupons(Long userId) {
        commerceClient.post()
                .uri("/internal/coupon/withdraw/{userId}/cleanup", userId)
                .retrieve().toBodilessEntity();
    }

    public void cleanupPoints(Long userId) {
        commerceClient.post()
                .uri("/internal/point/withdraw/{userId}/cleanup", userId)
                .retrieve().toBodilessEntity();
    }

    // ── 보상 트랜잭션 (PENDING → ACTIVE) ──

    public void restoreReviews(Long userId) {
        contentClient.post()
                .uri("/internal/review/withdraw/{userId}/restore", userId)
                .retrieve().toBodilessEntity();
    }

    public void restoreLikes(Long userId) {
        contentClient.post()
                .uri("/internal/like/withdraw/{userId}/restore", userId)
                .retrieve().toBodilessEntity();
    }

    public void restoreCoupons(Long userId) {
        commerceClient.post()
                .uri("/internal/coupon/withdraw/{userId}/restore", userId)
                .retrieve().toBodilessEntity();
    }

    public void restorePoints(Long userId) {
        commerceClient.post()
                .uri("/internal/point/withdraw/{userId}/restore", userId)
                .retrieve().toBodilessEntity();
    }

    // ── 최종 정리 (PENDING 데이터 확정 삭제) ──

    public void finalizeReviews(Long userId) {
        contentClient.post()
                .uri("/internal/review/withdraw/{userId}/finalize", userId)
                .retrieve().toBodilessEntity();
    }

    public void finalizeLikes(Long userId) {
        contentClient.post()
                .uri("/internal/like/withdraw/{userId}/finalize", userId)
                .retrieve().toBodilessEntity();
    }

    public void finalizeCoupons(Long userId) {
        commerceClient.post()
                .uri("/internal/coupon/withdraw/{userId}/finalize", userId)
                .retrieve().toBodilessEntity();
    }

    public void finalizePoints(Long userId) {
        commerceClient.post()
                .uri("/internal/point/withdraw/{userId}/finalize", userId)
                .retrieve().toBodilessEntity();
    }

    // ── 소셜 연동 해제 ──

    public void revokeSocialAccount(Long userId) {
        // Get provider info from user-service, then revoke via auth-service
        String provider = userClient.get()
                .uri("/internal/user/{userId}/provider", userId)
                .retrieve().body(String.class);
        String providerToken = userClient.get()
                .uri("/internal/user/{userId}/provider-token", userId)
                .retrieve().body(String.class);

        authClient.post()
                .uri("/internal/auth/revoke-social")
                .body(new RevokeSocialRequest(provider, providerToken))
                .retrieve().toBodilessEntity();
    }

    // ── 토큰 무효화 ──

    public void invalidateToken(String accessToken, String refreshToken) {
        authClient.post()
                .uri("/internal/auth/invalidate-access")
                .body(new TokenRequest(accessToken))
                .retrieve().toBodilessEntity();

        if (refreshToken != null) {
            authClient.post()
                    .uri("/internal/auth/delete-refresh")
                    .body(new TokenRequest(refreshToken))
                    .retrieve().toBodilessEntity();
        }
    }

    private record RevokeSocialRequest(String provider, String providerIdOrToken) {}
    private record TokenRequest(String token) {}
}
