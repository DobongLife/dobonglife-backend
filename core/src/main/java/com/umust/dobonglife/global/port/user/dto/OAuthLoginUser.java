package com.umust.dobonglife.global.port.user.dto;

import com.umust.dobonglife.global.common.constant.Role;

public record OAuthLoginUser(Long id, String name, Role role) {}
