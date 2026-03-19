package com.umust.dobonglife.global.port.user;

public interface UserPort {
    boolean isBlockedUser(Long userId);
    String getFcmToken(Long userId);
}
