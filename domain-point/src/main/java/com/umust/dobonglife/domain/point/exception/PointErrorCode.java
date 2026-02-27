package com.umust.dobonglife.domain.point.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PointErrorCode implements ErrorCode {

    INVALID_POINT(HttpStatus.BAD_REQUEST, "포인트가 부족합니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "포인트를 찾을 수 없습니다."),
    POINT_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 포인트입니다."),
    POINT_CANNOT_NEGATIVE(HttpStatus.CONFLICT, "포인트 내역은 음수가 될 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
