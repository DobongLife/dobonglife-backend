package com.umust.dobonglife.domain.place.exception;

import com.umust.dobonglife.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PlaceErrorCode implements ErrorCode {

    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소를 찾을 수 없습니다."),
    PLACE_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "장소 등록에는 최소 한 장의 사진이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getStatus() { return httpStatus.value(); }

    @Override
    public String getCode() { return name(); }
}
