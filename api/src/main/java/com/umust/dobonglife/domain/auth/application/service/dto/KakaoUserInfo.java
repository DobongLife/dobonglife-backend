package com.umust.dobonglife.domain.auth.application.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfo(
        Long id,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,

            @JsonProperty("email_needs_agreement")
            Boolean emailNeedsAgreement,

            @JsonProperty("is_email_valid")
            Boolean isEmailValid,

            @JsonProperty("is_email_verified")
            Boolean isEmailVerified,

            Profile profile
    ) {}

    public record Profile(
            String nickname,

            @JsonProperty("profile_image_url")
            String profileImageUrl,

            @JsonProperty("thumbnail_image_url")
            String thumbnailImageUrl
    ) {}
}
