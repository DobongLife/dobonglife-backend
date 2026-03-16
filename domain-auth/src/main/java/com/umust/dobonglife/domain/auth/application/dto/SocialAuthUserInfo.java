package com.umust.dobonglife.domain.auth.application.dto;

public record SocialAuthUserInfo(
        String providerId,
        String email,
        String name
) {
}
