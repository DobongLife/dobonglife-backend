package com.umust.dobonglife.global.port.auth.in;

import com.umust.dobonglife.global.common.constant.Provider;

public interface RevokeSocialAccountUseCase {
    void revoke(Provider provider, String providerIdOrToken);
}
