package com.umust.dobonglife.domain.review.application.port.out;

import com.umust.dobonglife.domain.review.domain.entity.Review;

public interface SaveReviewPort {

    Review save(Review review);
}
