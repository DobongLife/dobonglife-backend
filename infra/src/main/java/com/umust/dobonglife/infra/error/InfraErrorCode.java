package com.umust.dobonglife.infra.error;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InfraErrorCode implements ErrorCode {

    // Image
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 이미지 파일입니다."),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원되지 않는 이미지 형식입니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 이미지가 없습니다."),

    // Firebase
    FIREBASE_INITIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Firebase 초기화에 실패했습니다."),
    FCM_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 메시지 전송을 실패했습니다."),
    FCM_TOPIC_SUBSCRIBE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 토픽 구독을 실패했습니다."),
    FCM_TOPIC_UNSUBSCRIBE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM 토픽 구독 취소를 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
