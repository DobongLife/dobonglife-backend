package com.umust.dobonglife.domain.auth.domain;

public record SocialUserInfo(
        String providerId,
        String email,
        String name
) {
}
