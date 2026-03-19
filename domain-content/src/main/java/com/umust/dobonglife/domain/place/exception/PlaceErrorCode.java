package com.umust.dobonglife.domain.place.exception;

import com.umust.dobonglife.global.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PlaceErrorCode implements ErrorCode {

    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다."),
    PLACE_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "장소 등록에는 최소 한 장의 사진이 필요합니다."),
    PLACE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 장소입니다."),
    PLACE_CACHE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "장소 캐시 처리 중 오류가 발생했습니다."),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "유효하지 않은 평점입니다."),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "유효하지 않은 위치 정보입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() { return httpStatus.value(); }

    @Override
    public String getCode() { return name(); }
}
