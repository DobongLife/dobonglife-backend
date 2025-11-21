package com.umust.dobonglife.domain.review.repository;

import com.umust.dobonglife.domain.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
