package com.umust.dobonglife.domain.user.application.port.in;

import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.global.common.constant.Provider;

public interface OAuthFindUserUseCase {
    OAuthLoginUser findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name);
}
