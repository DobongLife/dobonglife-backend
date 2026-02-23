package com.umust.dobonglife.global.error.exception;

import com.umust.dobonglife.global.common.response.BaseErrorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.umust.dobonglife.global.error.ErrorCode.*;
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    // 404: 존재하지 않는 API (설정이 있어야 NoHandlerFoundException이 발생할 수 있음)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseErrorResponse> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("[NoHandlerFound] {}", e.getRequestURL());
        return ResponseEntity
                .status(API_NOT_FOUND.getHttpStatus())
                .body(new BaseErrorResponse(API_NOT_FOUND));
    }

    // 405: 메서드 미지원
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("[MethodNotAllowed] {}", e.getMessage());
        return ResponseEntity
                .status(METHOD_NOT_ALLOWED.getHttpStatus())
                .body(new BaseErrorResponse(METHOD_NOT_ALLOWED));
    }

    // 400: @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        log.warn("[Validation] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getHttpStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    // 400: 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("[MissingParam] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getHttpStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    // 400: 타입 미스매치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("[TypeMismatch] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getHttpStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    // 400: JSON 파싱 실패 등 (request body 읽기 실패)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("[NotReadable] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getHttpStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    // 400: 개발자가 던진 잘못된 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[IllegalArgument] {}", e.getMessage());
        return ResponseEntity
                .status(BAD_REQUEST.getHttpStatus())
                .body(new BaseErrorResponse(BAD_REQUEST));
    }

    // BusinessException: 의도된 도메인 에러
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseErrorResponse> handleBusiness(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        // 비즈니스 예외는 보통 warn (서버 버그가 아니라 "정상적인 실패"일 수 있음)
        log.warn("[BusinessException] {} - {}", code.name(), code.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new BaseErrorResponse(code));
    }

    // 나머지 전부: 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseErrorResponse> handleException(Exception e) {
        log.error("[UnhandledException]", e);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(new BaseErrorResponse(INTERNAL_SERVER_ERROR));
    }
}
