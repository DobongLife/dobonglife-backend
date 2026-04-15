package com.umust.dobonglife.presentation.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class ServiceHealthController {

    private final Map<String, String> serviceUrls;

    public ServiceHealthController(
            @Value("${service.auth.url}") String authUrl,
            @Value("${service.user.url}") String userUrl,
            @Value("${service.commerce.url}") String commerceUrl,
            @Value("${service.content.url}") String contentUrl,
            @Value("${service.notification.url}") String notificationUrl) {
        this.serviceUrls = Map.of(
                "auth-service", authUrl,
                "user-service", userUrl,
                "commerce-service", commerceUrl,
                "content-service", contentUrl,
                "notification-service", notificationUrl
        );
    }

    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> checkAllServices() {
        Map<String, CompletableFuture<Map<String, Object>>> futures = new LinkedHashMap<>();

        for (var entry : serviceUrls.entrySet()) {
            futures.put(entry.getKey(),
                    CompletableFuture.supplyAsync(() -> checkService(entry.getKey(), entry.getValue())));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        boolean allUp = true;

        for (var entry : futures.entrySet()) {
            Map<String, Object> status = entry.getValue().join();
            result.put(entry.getKey(), status);
            if (!"UP".equals(status.get("status"))) {
                allUp = false;
            }
        }

        result.put("overall", allUp ? "ALL_UP" : "PARTIAL_DOWN");
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> checkService(String name, String baseUrl) {
        Instant start = Instant.now();
        try {
            RestClient client = RestClient.builder()
                    .baseUrl(baseUrl)
                    .build();

            client.get()
                    .uri("/actuator/health")
                    .retrieve()
                    .toBodilessEntity();

            long latency = Duration.between(start, Instant.now()).toMillis();
            return Map.of("status", "UP", "url", baseUrl, "latencyMs", latency);
        } catch (Exception e) {
            long latency = Duration.between(start, Instant.now()).toMillis();
            log.warn("[HealthCheck] {} 연결 실패: {}", name, e.getMessage());
            return Map.of("status", "DOWN", "url", baseUrl, "latencyMs", latency, "error", e.getMessage());
        }
    }
}
