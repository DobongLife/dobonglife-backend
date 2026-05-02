package com.umust.dobonglife.domain.auth.presentation.dto.request;

public record RevokeSocialAccountRequest(String provider, String providerIdOrToken) {}
