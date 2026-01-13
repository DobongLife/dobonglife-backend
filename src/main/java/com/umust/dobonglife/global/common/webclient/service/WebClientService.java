package com.umust.dobonglife.global.common.webclient.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@Component
public class WebClientService {

    public static final String OPEN_API_SECRET_KEY = "hv5UBK8iLZSVADinVY4DMao1hpAK0razW7E8nDAcckfuzs2mg2tDHdCU6rLpsJrF+PilMuQ0K/Qfij8gD8UUVQ==";
    public static final WebClient OPEN_API_WEBCLIENT = WebClient.builder()
            .baseUrl("https://api.odcloud.kr/api/nts-businessman/v1/")
            .build();

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

    private URI generateBusinessStatusRequestURI(UriBuilder uriBuilder) {
        URI uri =  uriBuilder.path("/status")
                .build();
        log.info("📡 최종 요청 URI: {}", uri);
        return uri;
    }
}

