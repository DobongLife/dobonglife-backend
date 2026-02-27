package com.umust.dobonglife.infra.webclient.service;

import com.umust.dobonglife.infra.webclient.business.dto.response.GeoPointResponse;
import io.netty.channel.ChannelOption;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
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

    private WebClient openApiWebClient;
    private WebClient naverMapWebClient;

    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

    @PostConstruct
    void init() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(READ_TIMEOUT);
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        this.openApiWebClient = WebClient.builder()
                .baseUrl(openApiBaseUrl)
                .clientConnector(connector)
                .build();
        this.naverMapWebClient = WebClient.builder()
                .baseUrl(naverMapBaseUrl)
                .clientConnector(connector)
                .filter(logRequest())
                .build();
    }

    public Map getCompanyStatus(String bsnsLcns) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("b_no", Collections.singletonList(bsnsLcns));
        log.info("Request body: {}", requestBody);
        return openApiWebClient.post()
                .uri(this::generateBusinessStatusRequestURI)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Infuser " + openApiSecretKey)
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
            return naverMapWebClient.get()
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

    private URI generateBusinessStatusRequestURI(UriBuilder uriBuilder) {
        URI uri = uriBuilder.path("/status").build();
        log.info("Open API 최종 요청 URI: {}", uri);
        return uri;
    }

    private URI generateNaverGeocodeURI(UriBuilder uriBuilder, String address) {
        URI uri = uriBuilder
                .path("/map-geocode/v2/geocode")
                .queryParam("query", address)
                .build();
        log.info("네이버 Geocode 최종 요청 URI: {}", uri);
        return uri;
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("REQUEST: {} {}", request.method(), request.url());
            log.info("HEADERS: {}", request.headers().keySet());
            return Mono.just(request);
        });
    }
}
