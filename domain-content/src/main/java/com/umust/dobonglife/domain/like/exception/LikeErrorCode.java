package com.umust.dobonglife.domain.like.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LikeErrorCode implements ErrorCode {

    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다."),
    LIKE_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 대상을 찾을 수 없습니다."),
    INVALID_TARGET_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 좋아요 대상 유형입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
