package com.umust.dobonglife.domain.auth.domain;

public record LoginSuccessCommand(
        Long userId,
        String provider,
        String role,
        String userName,
        String fcmToken
) {
}
