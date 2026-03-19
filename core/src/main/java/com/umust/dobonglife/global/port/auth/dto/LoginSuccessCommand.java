package com.umust.dobonglife.global.port.auth.dto;

public record LoginSuccessCommand(Long userId, String provider, String role, String userName) {}
