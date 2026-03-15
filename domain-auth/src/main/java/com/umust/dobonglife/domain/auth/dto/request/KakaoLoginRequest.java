package com.umust.dobonglife.domain.auth.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoLoginRequest {
    private String accessToken;
    private String fcmToken;
}
