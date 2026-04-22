package com.umust.dobonglife.global.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"success", "message", "data"})
public class BaseResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    @JsonCreator
    private BaseResponse(
            @JsonProperty("success") boolean success,
            @JsonProperty("message") String message,
            @JsonProperty("data") T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    private BaseResponse(T data, String message) {
        this(true, message, data);
    }

    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(data, "요청에 성공하였습니다.");
    }

    public static <T> BaseResponse<T> ok(T data, String message) {
        return new BaseResponse<>(data, message);
    }
}