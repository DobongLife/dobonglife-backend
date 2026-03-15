package com.umust.dobonglife.global.error;

import com.umust.dobonglife.global.common.error.ErrorCode;
import com.umust.dobonglife.global.common.response.BaseErrorResponse;
import com.umust.dobonglife.global.common.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

import static com.umust.dobonglife.global.common.error.CommonErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseErrorResponse> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("[NoHandlerFound] {}", e.getRequestURL());
        return ResponseEntity
                .status(API_NOT_FOUND.getStatus())
                .body(new BaseErrorResponse(API_NOT_FOUND));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("[MethodNotAllowed] {}", e.getMessage());
        return ResponseEntity
                .status(METHOD_NOT_ALLOWED.getStatus())
                .body(new BaseErrorResponse(METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[Validation] {}", detail);
        return ResponseEntity
                .status(BAD_REQUEST.getStatus())
                .body(new BaseErrorResponse(BAD_REQUEST, detail));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("[MissingParam] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("[TypeMismatch] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("[NotReadable] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseErrorResponse> handleBusiness(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        log.warn("[BusinessException] {} - {}", code.getCode(), code.getMessage());
        return ResponseEntity
                .status(code.getStatus())
                .body(new BaseErrorResponse(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseErrorResponse> handleException(Exception e) {
        log.error("[UnhandledException]", e);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .body(new BaseErrorResponse(INTERNAL_SERVER_ERROR));
    }
}
