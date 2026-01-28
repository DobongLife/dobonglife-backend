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

    @Schema(description = "이메일", example = "dobonlife@gmail.com")
    @NotNull
    private String email;

    @Schema(description = "인증 번호(인증 코드)", example = "85mNvlC5")
    @NotNull
    private String authCode;
}
