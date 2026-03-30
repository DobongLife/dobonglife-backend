package com.umust.dobonglife.domain.like.application.port.in;

import com.umust.dobonglife.global.common.constant.TargetType;

public interface ToggleLikeUseCase {

    boolean toggleLike(Long userId, TargetType targetType, Long targetId);
}
