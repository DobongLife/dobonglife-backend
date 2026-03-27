package com.umust.dobonglife.domain.user.application.port.in;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;

public interface GetUserUseCase {
    String getProviderId(Long userId);
    Provider getProvider(Long userId);
    boolean isBlockedUser(Long userId);
    boolean isNotActiveUser(Long userId);
    String getFcmToken(Long userId);
}
