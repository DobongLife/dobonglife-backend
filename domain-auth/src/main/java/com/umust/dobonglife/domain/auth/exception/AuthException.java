package com.umust.dobonglife.domain.auth.exception;

import com.umust.dobonglife.global.common.error.exception.BusinessException;

public class AuthException extends BusinessException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
