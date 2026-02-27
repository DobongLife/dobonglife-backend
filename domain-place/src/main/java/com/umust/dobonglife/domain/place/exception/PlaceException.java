package com.umust.dobonglife.domain.place.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;

public class PlaceException extends BusinessException {

    public PlaceException(PlaceErrorCode errorCode) {
        super(errorCode);
    }

    public PlaceException(PlaceErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
