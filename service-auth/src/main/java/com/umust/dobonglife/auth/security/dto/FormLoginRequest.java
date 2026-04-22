package com.umust.dobonglife.auth.security.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FormLoginRequest {
    private String email;
    private String password;
    private String fcmToken;
}
