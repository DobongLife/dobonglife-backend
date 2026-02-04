package com.umust.dobonglife.global.common.webclient.service;

import com.umust.dobonglife.global.common.webclient.business.dto.response.GeoPointResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

@Slf4j
@Component
public class WebClientService {

    public static final String OPEN_API_SECRET_KEY = "hv5UBK8iLZSVADinVY4DMao1hpAK0razW7E8nDAcckfuzs2mg2tDHdCU6rLpsJrF+PilMuQ0K/Qfij8gD8UUVQ==";
    public static final WebClient OPEN_API_WEBCLIENT = WebClient.builder()
            .baseUrl("https://api.odcloud.kr/api/nts-businessman/v1/")
            .build();

    private static final WebClient NAVER_MAP_WEBCLIENT = WebClient.builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com")
            .filter(logRequest())
            .build();

    @Value("${naver.map.client-id:}")
    private String naverMapClientId;

    @Value("${naver.map.client-secret:}")
    private String naverMapClientSecret;

    public Map getCompanyStatus(String bsnsLcns) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("b_no", Collections.singletonList(bsnsLcns));
        log.info("Request body: {}", requestBody);
        return OPEN_API_WEBCLIENT.post()
                .uri(this::generateBusinessStatusRequestURI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Infuser " + OPEN_API_SECRET_KEY)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

    }

    public Map geocodeByNaver(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("주소(address)는 필수입니다.");
        }
        if (naverMapClientId == null || naverMapClientId.isBlank()
                || naverMapClientSecret == null || naverMapClientSecret.isBlank()) {
            throw new IllegalStateException("네이버 지도 API 키가 설정되어 있지 않습니다. (naver.map.client-id / client-secret)");
        }

        log.info("네이버 Geocoding 요청 address={}", address);
        try {
            return NAVER_MAP_WEBCLIENT.get()
                    .uri(uriBuilder -> generateNaverGeocodeURI(uriBuilder, address))
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header("x-ncp-apigw-api-key-id", naverMapClientId)
                    .header("x-ncp-apigw-api-key", naverMapClientSecret)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class).flatMap(body -> {
                                log.error("Naver Geocode ERROR status={}, body={}", resp.statusCode(), body);
                                return resp.createException();
                            })
                    )
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException e) {
            // 여기로 오면 status + response body가 더 확실히 찍힘
            log.error("Naver Geocode WebClientResponseException status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Naver Geocode Unknown Exception", e);
            throw e;
        }

    }

    public Optional<GeoPointResponse> geocodePoint(String address) {
        Map result = geocodeByNaver(address);

        Object addressesObj = result.get("addresses");
        if (!(addressesObj instanceof List<?> addresses) || addresses.isEmpty()) {
            return Optional.empty();
        }

        Object firstObj = addresses.get(0);
        if (!(firstObj instanceof Map<?, ?> first)) {
            return Optional.empty();
        }

        Object x = first.get("x"); // 경도 (lon)
        Object y = first.get("y"); // 위도 (lat)
        if (x == null || y == null) return Optional.empty();

        try {
            double lon = Double.parseDouble(String.valueOf(x));
            double lat = Double.parseDouble(String.valueOf(y));
            return Optional.of(new GeoPointResponse(lat, lon));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private URI generateBusinessStatusRequestURI(UriBuilder uriBuilder) {
        URI uri =  uriBuilder.path("/status")
                .build();
        log.info("📡 Open API 최종 요청 URI: {}", uri);
        return uri;
    }

    private URI generateNaverGeocodeURI(UriBuilder uriBuilder, String address) {
        URI uri = uriBuilder
                .path("/map-geocode/v2/geocode")
                .queryParam("query", address)
                .build();
        log.info("📡 네이버 Geocode 최종 요청 URI: {}", uri);
        return uri;
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("➡️ REQUEST: {} {}", request.method(), request.url());
            log.info("➡️ HEADERS: {}", request.headers().keySet());
            return Mono.just(request);
        });
    }
}

