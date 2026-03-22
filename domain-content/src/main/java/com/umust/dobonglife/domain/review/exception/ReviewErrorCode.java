package com.umust.dobonglife.domain.review.exception;

import com.umust.dobonglife.global.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    NOT_REVIEW_OWNER(HttpStatus.FORBIDDEN, "해당 리뷰에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() { return httpStatus.value(); }

    @Override
    public String getCode() { return name(); }
}
