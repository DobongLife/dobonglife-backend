package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.domain.AuthUserInfo;
import com.umust.dobonglife.global.common.constant.Provider;

public interface AuthUserPort {

    AuthUserInfo loadLocalUserByEmail(String email);

    AuthUserInfo findOrCreateOAuthUser(Provider provider, String providerId, String email, String name);

    void updateFcmToken(Long userId, String fcmToken);
}
