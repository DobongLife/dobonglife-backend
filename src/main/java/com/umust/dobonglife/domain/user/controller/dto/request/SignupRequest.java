package com.umust.dobonglife.domain.user.controller.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {
    private String email;
    private String name;
    private String password;
}
