package com.umust.dobonglife.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    REVIEW_POINT_SUCCESS("100 포인트가 적립되었습니다");

    private final String message;
}
