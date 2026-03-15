package com.umust.dobonglife.global.common.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({"success", "status", "code", "message", "timestamp"})
public class BaseErrorResponse {

    private final boolean success;
    private final int status;
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    public BaseErrorResponse(ErrorCode errorCode) {
        this.success = false;
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public BaseErrorResponse(ErrorCode errorCode, String customMessage) {
        this.success = false;
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.message = customMessage;
        this.timestamp = LocalDateTime.now();
    }
}
