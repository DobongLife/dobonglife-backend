package com.umust.dobonglife.global.port.user;

import com.umust.dobonglife.global.common.constant.Provider;

public interface UserPort {
    boolean isBlockedUser(Long userId);
    String getFcmToken(Long userId);
    Provider getProvider(Long userId);
    String getProviderId(Long userId);
    String getProviderToken(Long userId);
}
