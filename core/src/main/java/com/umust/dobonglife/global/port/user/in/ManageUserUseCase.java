package com.umust.dobonglife.global.port.user.in;

public interface ManageUserUseCase {
    void canExchangeCoupon(Long userId);
    void updateNotificationSetting(Long userId, boolean enabled);
    void updateFcmToken(Long userId, String fcmToken);
    void updateProviderToken(Long userId, String providerToken);
    void inValidFcmToken(Long userId);
}
