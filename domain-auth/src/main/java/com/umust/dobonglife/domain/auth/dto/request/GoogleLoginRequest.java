package com.umust.dobonglife.domain.auth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleLoginRequest {
    private String idToken;
    private String fcmToken;
}
