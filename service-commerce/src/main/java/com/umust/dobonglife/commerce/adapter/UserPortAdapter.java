package com.umust.dobonglife.commerce.adapter;

import com.umust.dobonglife.commerce.client.UserServiceClient;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * MSA adapter: domain-commerce의 ExchangeOrchestrator가 ManageUserUseCase.canExchangeCoupon()을 호출하는데,
 * 이를 REST 호출로 대체하는 어댑터.
 */
@Component
@RequiredArgsConstructor
public class UserPortAdapter implements ManageUserUseCase {

    private final UserServiceClient userServiceClient;

    @Override
    public void canExchangeCoupon(Long userId) {
        userServiceClient.canExchangeCoupon(userId);
    }

    @Override
    public void updateNotificationSetting(Long userId, boolean enabled) {
        throw new UnsupportedOperationException("Not supported in commerce-service");
    }

    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        throw new UnsupportedOperationException("Not supported in commerce-service");
    }

    @Override
    public void updateProviderToken(Long userId, String providerToken) {
        throw new UnsupportedOperationException("Not supported in commerce-service");
    }

    @Override
    public void inValidFcmToken(Long userId) {
        throw new UnsupportedOperationException("Not supported in commerce-service");
    }
}
