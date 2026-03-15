package com.umust.dobonglife.domain.auth.application.dto;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;

public record AuthUserInfo(
        Long userId,
        String email,
        String password,
        String name,
        Provider provider,
        Role role
) {
}
