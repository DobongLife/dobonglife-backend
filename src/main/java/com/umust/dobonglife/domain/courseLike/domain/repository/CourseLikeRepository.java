package com.umust.dobonglife.domain.courseLike.domain.repository;

import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
