package com.umust.dobonglife.domain.auth.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    LOCAL("local"),
    NAVER("naver"),
    KAKAO("kakao"),
    GOOGLE("google");

    private final String value;
}