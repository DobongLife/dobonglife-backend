package com.umust.dobonglife.domain.auth.application.port.out;

import com.umust.dobonglife.domain.auth.application.dto.AuthUserInfo;
import com.umust.dobonglife.global.common.constant.Provider;

public interface AuthUserPort {

    AuthUserInfo loadLocalUserByEmail(String email);
    void updateFcmToken(Long userId, String fcmToken);
}
