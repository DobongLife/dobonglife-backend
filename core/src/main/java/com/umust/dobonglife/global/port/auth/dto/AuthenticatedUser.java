package com.umust.dobonglife.global.port.auth.dto;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;

public record AuthenticatedUser(Long userId, String userName, Role role, Provider provider) {}
