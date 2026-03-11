package com.umust.dobonglife.domain.point.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;

public class PointException extends BusinessException {

    public PointException(PointErrorCode errorCode) {
        super(errorCode);
    }

    public PointException(PointErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
