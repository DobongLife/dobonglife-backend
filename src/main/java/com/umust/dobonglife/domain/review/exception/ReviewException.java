package com.umust.dobonglife.domain.review.exception;

import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.error.ErrorCode;

public class ReviewException extends BusinessException {
    public ReviewException(ErrorCode code) {
        super(code);
    }
}
