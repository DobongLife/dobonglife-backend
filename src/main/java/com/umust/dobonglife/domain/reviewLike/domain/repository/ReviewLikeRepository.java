package com.umust.dobonglife.domain.reviewLike.domain.repository;

import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import com.umust.dobonglife.domain.reviewLike.domain.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<ReviewLike> findByCourseIdAndUserId(Long courseId, Long userId);
}
