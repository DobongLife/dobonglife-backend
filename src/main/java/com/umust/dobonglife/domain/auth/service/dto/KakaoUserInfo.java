package com.umust.dobonglife.domain.auth.service.dto;

public record KakaoUserInfo(
        Long id,
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            Profile profile
    ) {
        public record Profile(String nickname) {}
    }
}
