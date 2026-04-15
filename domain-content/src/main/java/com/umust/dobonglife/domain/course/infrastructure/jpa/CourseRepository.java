package com.umust.dobonglife.domain.course.infrastructure.jpa;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.infrastructure.jpa.custom.CourseRepositoryCustom;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {

    Optional<Course> findByIdAndStatus(Long id, BaseStatus status);

    long countByUserIdAndStatus(Long userId, BaseStatus status);

    @Modifying
    @Query("""
        UPDATE Course c SET
            c.reviewCount = c.reviewCount + 1,
            c.ratingSum = c.ratingSum + :rating,
            c.averageRating = ROUND((c.ratingSum + :rating) / (c.reviewCount + 1), 1)
        WHERE c.id = :courseId
    """)
    void addReview(@Param("courseId") Long courseId, @Param("rating") Double rating);

    @Modifying
    @Query("""
        UPDATE Course c SET
            c.reviewCount = c.reviewCount - 1,
            c.ratingSum = c.ratingSum - :rating,
            c.averageRating = CASE WHEN c.reviewCount > 1
                THEN ROUND((c.ratingSum - :rating) / (c.reviewCount - 1), 1)
                ELSE 0.0 END
        WHERE c.id = :courseId AND c.reviewCount > 0
    """)
    void removeReview(@Param("courseId") Long courseId, @Param("rating") Double rating);
}
