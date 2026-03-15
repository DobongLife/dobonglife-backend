package com.umust.dobonglife.domain.auth.application.dto;

public record SocialUserInfo(
        String providerId,
        String email,
        String name
) {
}
