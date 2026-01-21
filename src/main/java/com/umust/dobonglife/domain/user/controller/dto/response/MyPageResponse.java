package com.umust.dobonglife.domain.user.controller.dto.response;

import com.umust.dobonglife.domain.user.domain.constant.Role;

import java.util.List;

public record MyPageResponse(
        String name,
        String email,
        Role role
) {
}
