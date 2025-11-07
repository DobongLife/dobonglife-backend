package com.umust.dobonglife.domain.auth.model.dto;

public interface OAuth2Response {

    String getProvider(); // 제공자
    String getProviderId(); // 제공자 부여 Id
    String getEmail();
    String getName();
}
