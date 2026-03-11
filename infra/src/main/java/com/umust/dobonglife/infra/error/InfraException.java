package com.umust.dobonglife.infra.error;

import com.umust.dobonglife.global.error.exception.BusinessException;

public class InfraException extends BusinessException {

    public InfraException(InfraErrorCode errorCode) {
        super(errorCode);
    }

    public InfraException(InfraErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
