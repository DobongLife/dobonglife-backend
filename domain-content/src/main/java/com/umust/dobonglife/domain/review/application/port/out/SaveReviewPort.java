package com.umust.dobonglife.domain.review.application.port.out;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.global.common.model.BaseStatus;

public interface SaveReviewPort {

    Review save(Review review);

    void updateStatusByUserId(Long userId, BaseStatus currentStatus, BaseStatus newStatus);

    void nullifyUserAndUpdateStatus(Long userId, BaseStatus currentStatus, BaseStatus newStatus);
}
