package com.umust.dobonglife.domain.auth.model.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {

        Object response = attribute.get("response");

        if(response instanceof Map) {
            this.attribute = (Map<String, Object>) response;
        } else {
            throw new IllegalArgumentException("Invalid response structure from Naver API");
        }
    }


    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
