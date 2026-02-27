package com.umust.dobonglife.domain.user.application.dto.request;

public record UserIsBlockedRequest(Long userId) {
    public static UserIsBlockedRequest of (Long userId){
        return new UserIsBlockedRequest(userId);
    }
}
