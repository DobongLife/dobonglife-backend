package com.umust.dobonglife.global.error.exception;


import com.umust.dobonglife.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public BusinessException(ErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
        this.errorCode = code;
    }
}