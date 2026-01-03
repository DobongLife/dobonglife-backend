package com.umust.dobonglife.domain.review.exception;

import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;

public class ReviewException extends BusinessException {
    public ReviewException(ErrorCode code) {
        super(code);
    }
}
