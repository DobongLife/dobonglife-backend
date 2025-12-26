package com.umust.dobonglife.domain.review.domain.repository.custom;

import com.umust.dobonglife.domain.review.service.dto.ReviewItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<ReviewItemProjection> findReviewItemsByUserId(Long userId, Pageable pageable);
}
