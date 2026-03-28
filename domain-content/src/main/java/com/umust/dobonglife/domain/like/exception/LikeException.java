package com.umust.dobonglife.domain.like.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;

public class LikeException extends BusinessException {

    public LikeException(LikeErrorCode errorCode) {
        super(errorCode);
    }

    public LikeException(LikeErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
