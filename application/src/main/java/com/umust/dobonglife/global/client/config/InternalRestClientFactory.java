package com.umust.dobonglife.global.client.config;

import com.umust.dobonglife.global.common.error.exception.ServiceCallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
public final class InternalRestClientFactory {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    private InternalRestClientFactory() {
    }

    public static RestClient create(String baseUrl, String serviceName) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        (request, response) -> {
                            int code = response.getStatusCode().value();
                            log.error("[{}] 내부 호출 실패: {} {} → {}",
                                    serviceName, request.getMethod(), request.getURI(), code);
                            throw new ServiceCallException(serviceName, code,
                                    new RuntimeException(serviceName + " 응답: " + code));
                        }
                )
                .build();
    }
}
