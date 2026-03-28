package com.umust.dobonglife.domain.user.application.dto;

import com.umust.dobonglife.global.common.constant.Role;

public record OAuthLoginUser(
        Long id,
        String name,
        Role role
) {
}
