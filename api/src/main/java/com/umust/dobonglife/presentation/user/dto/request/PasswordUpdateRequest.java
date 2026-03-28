package com.umust.dobonglife.presentation.user.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordUpdateRequest {
    private String email;
    private String authCode;
    private String newPassword;
}
