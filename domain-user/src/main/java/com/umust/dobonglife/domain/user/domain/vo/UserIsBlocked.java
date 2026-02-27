package com.umust.dobonglife.domain.user.domain.vo;

public record UserIsBlocked(boolean isBlocked) {
    public static UserIsBlocked of(boolean isBlocked){
        return new UserIsBlocked(isBlocked);
    }
}
