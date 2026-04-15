package com.umust.dobonglife.domain.user.presentation.dto.request;

public record AuthCodeCheckRequest(String email, String authCode, boolean forSignUp) {}
