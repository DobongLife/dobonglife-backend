package com.umust.dobonglife.domain.user.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailCodeCheckRequest {

    @Schema(description = "이메일", example = "dobonglife@gmail.com")
    @NotNull
    private String email;

    @Schema(description = "인증 번호(인증 코드) 6자리", example = "123456")
    @NotNull
    private String authCode;

    @Schema(description = "회원가입 여부")
    @NotNull
    private boolean forSignUp;
}
