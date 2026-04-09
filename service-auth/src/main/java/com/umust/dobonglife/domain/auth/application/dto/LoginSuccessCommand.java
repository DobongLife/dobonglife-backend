package com.umust.dobonglife.domain.auth.application.dto;

public record LoginSuccessCommand(
        Long userId,
        String provider,
        String role,
        String userName
) {
}
