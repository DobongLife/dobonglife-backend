package com.umust.dobonglife.global.port.auth.dto;

public record AuthTokens(String accessToken, String refreshToken, String role) {}
