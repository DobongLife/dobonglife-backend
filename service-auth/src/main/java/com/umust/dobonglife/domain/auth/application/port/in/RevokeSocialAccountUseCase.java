package com.umust.dobonglife.domain.auth.application.port.in;

import com.umust.dobonglife.global.common.constant.Provider;

public interface RevokeSocialAccountUseCase {

    void revoke(Provider provider, String providerIdOrToken);
}
