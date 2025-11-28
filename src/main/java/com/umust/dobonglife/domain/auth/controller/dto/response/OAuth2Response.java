package com.umust.dobonglife.domain.auth.controller.dto.response;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;

public interface OAuth2Response {

    Provider getProvider(); // 제공자
    String getProviderId(); // 제공자 부여 Id
    String getEmail();
    String getName();
}
