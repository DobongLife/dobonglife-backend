package com.umust.dobonglife.global.port;

public interface UserPort {

    void validateCouponExchange(Long userId);

    boolean isUserBlocked(Long userId);

    String getFcmToken(Long userId);
}
