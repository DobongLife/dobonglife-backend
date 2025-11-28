package com.umust.dobonglife.domain.review.domain.repository;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
