package com.umust.dobonglife.domain.user.infrastructure.adapter;

import com.umust.dobonglife.domain.user.application.UserService;
import com.umust.dobonglife.global.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPortAdapter implements UserPort {

    private final UserService userService;

    @Override
    public void validateCouponExchange(Long userId) {
        userService.validateCouponExchange(userId);
    }

    @Override
    public boolean isUserBlocked(Long userId) {
        return userService.isUserBlocked(userId);
    }

    @Override
    public String getFcmToken(Long userId) {
        return userService.getFcmToken(userId);
    }
}
