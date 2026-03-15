package com.umust.dobonglife.domain.auth.dto.response;

import com.umust.dobonglife.global.common.constant.Provider;

public interface OAuth2Response {

    Provider getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
