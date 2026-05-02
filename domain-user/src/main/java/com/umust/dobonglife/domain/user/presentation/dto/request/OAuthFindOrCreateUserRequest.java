package com.umust.dobonglife.domain.user.presentation.dto.request;

public record OAuthFindOrCreateUserRequest(String provider, String providerUserId, String email, String name) {}
