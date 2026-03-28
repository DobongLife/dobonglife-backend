package com.umust.dobonglife.domain.user.application.port.in;

public interface ManageUserUseCase {
    void canExchangeCoupon(Long userId);
    void updateNotificationSetting(Long userId, boolean enabled);
    void updateFcmToken(Long userId, String fcmToken);
    void updateProviderToken(Long userId, String providerToken);
    void inValidFcmToken(Long userId);
}
