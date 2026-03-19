package com.umust.dobonglife.domain.course.domain.repository;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
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
            c.reviewCount = CASE WHEN c.reviewCount > 0 THEN c.reviewCount - 1 ELSE 0 END,
            c.ratingSum = CASE WHEN c.ratingSum >= :rating THEN c.ratingSum - :rating ELSE 0 END,
            c.averageRating = CASE WHEN c.reviewCount > 1
                THEN ROUND((c.ratingSum - :rating) / (c.reviewCount - 1), 1)
                ELSE 0.0 END
        WHERE c.id = :courseId
    """)
    void removeReview(@Param("courseId") Long courseId, @Param("rating") Double rating);
}
