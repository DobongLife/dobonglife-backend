package com.umust.dobonglife.domain.like.application.port.out;

import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.global.common.model.BaseStatus;

public interface SaveLikePort {

    Like save(Like like);

    void delete(Like like);

    void updateStatusByUserId(Long userId, BaseStatus currentStatus, BaseStatus newStatus);

    void deleteByUserIdAndStatus(Long userId, BaseStatus status);
}
