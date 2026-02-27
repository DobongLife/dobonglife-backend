package com.umust.dobonglife.global.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String name();
}
