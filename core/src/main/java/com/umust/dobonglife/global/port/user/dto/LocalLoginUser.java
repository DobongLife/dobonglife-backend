package com.umust.dobonglife.global.port.user.dto;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;

public record LocalLoginUser(Long userId, String email, String password, Provider provider, Role role) {}
