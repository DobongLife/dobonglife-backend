package com.umust.dobonglife.domain.auth.controller.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppleLoginRequest {
    private String identityToken;
    private String fcmToken;
}
