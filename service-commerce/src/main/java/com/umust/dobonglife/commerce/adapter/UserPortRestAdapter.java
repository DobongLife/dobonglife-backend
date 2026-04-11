package com.umust.dobonglife.commerce.adapter;

import com.umust.dobonglife.commerce.client.UserServiceClient;
import com.umust.dobonglife.global.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPortRestAdapter implements UserPort {

    private final UserServiceClient userServiceClient;

    @Override
    public void canExchangeCoupon(Long userId) {
        userServiceClient.canExchangeCoupon(userId);
    }
}
