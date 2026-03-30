package com.umust.dobonglife.domain.review.infrastructure.jpa;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.infrastructure.jpa.custom.ReviewRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
}
