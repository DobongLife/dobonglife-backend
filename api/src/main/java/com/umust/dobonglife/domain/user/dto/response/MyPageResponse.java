package com.umust.dobonglife.domain.user.dto.response;

import com.umust.dobonglife.global.common.constant.Role;

public record MyPageResponse(
        String name,
        String email,
        Role role
) {
}
