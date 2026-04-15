package com.umust.dobonglife.domain.user.presentation.dto.request;

public record PasswordUpdateRequest(String email, String authCode, String newPassword) {}
