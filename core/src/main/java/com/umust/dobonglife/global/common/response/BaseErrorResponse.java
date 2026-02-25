package com.umust.dobonglife.global.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({"success", "code", "message", "timestamp"})
public class BaseErrorResponse {

    private final boolean success;
    private final int code;
    private final String message;
    private final LocalDateTime timestamp;

    public BaseErrorResponse(ErrorCode errorCode) {
        this.success = false;
        this.code = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public BaseErrorResponse(ErrorCode errorCode, String customMessage) {
        this.success = false;
        this.code = errorCode.getHttpStatus().value();
        this.message = customMessage;
        this.timestamp = LocalDateTime.now();
    }
}
