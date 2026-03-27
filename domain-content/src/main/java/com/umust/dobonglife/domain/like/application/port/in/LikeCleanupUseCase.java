package com.umust.dobonglife.domain.like.application.port.in;

public interface LikeCleanupUseCase {

    void deleteByUserId(Long userId);
}
