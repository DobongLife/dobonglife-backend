package com.umust.dobonglife.domain.business.exception;

public class BusinessException extends com.umust.dobonglife.global.common.error.exception.BusinessException {

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(BusinessErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
