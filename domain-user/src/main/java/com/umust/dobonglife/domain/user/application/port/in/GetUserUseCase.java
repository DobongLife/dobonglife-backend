package com.umust.dobonglife.domain.user.application.port.in;

import com.umust.dobonglife.domain.user.domain.entity.User;

public interface GetUserUseCase {
    User findById(Long userId);
    boolean isBlockedUser(Long userId);
    String getFcmToken(Long userId);
}
