package com.umust.dobonglife.domain.business.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;

public class BusinessDomainException extends BusinessException {

    public BusinessDomainException(BusinessErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessDomainException(BusinessErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
