package com.umust.dobonglife.global.client.content;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.port.content.BannerPort;
import com.umust.dobonglife.global.port.dto.content.BannerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ContentBannerClient implements BannerPort {

    private final RestClient restClient;

    public ContentBannerClient(@Value("${service.content.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "content-service");
    }

    @Override
    public List<BannerInfo> getActiveBanners() {
        return restClient.get()
                .uri("/internal/banner/active")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
