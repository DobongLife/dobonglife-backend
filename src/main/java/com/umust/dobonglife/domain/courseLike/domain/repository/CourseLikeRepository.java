package com.umust.dobonglife.domain.courseLike.domain.repository;

import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CourseLikeRepository extends JpaRepository<CourseLike, Long> {
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<CourseLike> findByCourseIdAndUserId(Long courseId, Long userId);

    @Query("SELECT cl.courseId FROM CourseLike cl WHERE cl.userId = :userId AND cl.courseId IN :courseIds")
    Set<Long> findLikedCourseIdsByUserIdAndCourseIds(Long userId, List<Long> courseIds);
}
