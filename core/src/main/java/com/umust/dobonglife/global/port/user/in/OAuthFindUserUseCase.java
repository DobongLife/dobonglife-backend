package com.umust.dobonglife.global.port.user.in;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.port.user.dto.OAuthLoginUser;

public interface OAuthFindUserUseCase {
    OAuthLoginUser findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name);
}
