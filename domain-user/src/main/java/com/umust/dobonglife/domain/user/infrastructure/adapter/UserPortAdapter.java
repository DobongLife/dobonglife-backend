package com.umust.dobonglife.domain.user.infrastructure.adapter;

import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.global.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPortAdapter implements UserPort {

    private final ManageUserUseCase manageUserUseCase;

    @Override
    public void canExchangeCoupon(Long userId) {
        manageUserUseCase.canExchangeCoupon(userId);
    }
}
