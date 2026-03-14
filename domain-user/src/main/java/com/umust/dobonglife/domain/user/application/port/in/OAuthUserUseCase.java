package com.umust.dobonglife.domain.user.application.port.in;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;

public interface OAuthUserUseCase {
    User findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name);
}
