package com.umust.dobonglife.infra.webclient.service;

import com.umust.dobonglife.infra.webclient.business.dto.response.GeoPointResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

@Slf4j
@Component
@ConditionalOnProperty(name = "open-api.secret-key")
public class WebClientService {

    @Value("${open-api.secret-key}")
    private String openApiSecretKey;

    @Value("${open-api.base-url}")
    private String openApiBaseUrl;

    @Value("${naver.map.base-url}")
    private String naverMapBaseUrl;

    @Value("${naver.map.client-id:}")
    private String naverMapClientId;

    @Value("${naver.map.client-secret:}")
    private String naverMapClientSecret;

    private RestClient openApiRestClient;
    private RestClient naverMapRestClient;

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 10_000;

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);

        this.openApiRestClient = RestClient.builder()
                .baseUrl(openApiBaseUrl)
                .requestFactory(factory)
                .build();

        this.naverMapRestClient = RestClient.builder()
                .baseUrl(naverMapBaseUrl)
                .requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    log.info("REQUEST: {} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getCompanyStatus(String bsnsLcns) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("b_no", Collections.singletonList(bsnsLcns));
        log.info("Request body: {}", requestBody);

        return openApiRestClient.post()
                .uri("/status")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Infuser " + openApiSecretKey)
                .body(requestBody)
                .retrieve()
                .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> geocodeByNaver(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("주소(address)는 필수입니다.");
        }
        if (naverMapClientId == null || naverMapClientId.isBlank()
                || naverMapClientSecret == null || naverMapClientSecret.isBlank()) {
            throw new IllegalStateException("네이버 지도 API 키가 설정되어 있지 않습니다.");
        }

        log.info("네이버 Geocoding 요청 address={}", address);
        try {
            return naverMapRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/map-geocode/v2/geocode")
                            .queryParam("query", address)
                            .build())
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header("x-ncp-apigw-api-key-id", naverMapClientId)
                    .header("x-ncp-apigw-api-key", naverMapClientSecret)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientResponseException e) {
            log.error("Naver Geocode error status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    public Optional<GeoPointResponse> geocodePoint(String address) {
        Map<String, Object> result = geocodeByNaver(address);

        Object addressesObj = result.get("addresses");
        if (!(addressesObj instanceof List<?> addresses) || addresses.isEmpty()) {
            return Optional.empty();
        }

        Object firstObj = addresses.get(0);
        if (!(firstObj instanceof Map<?, ?> first)) {
            return Optional.empty();
        }

        Object x = first.get("x");
        Object y = first.get("y");
        if (x == null || y == null) return Optional.empty();

        try {
            double lon = Double.parseDouble(String.valueOf(x));
            double lat = Double.parseDouble(String.valueOf(y));
            return Optional.of(new GeoPointResponse(lat, lon));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
