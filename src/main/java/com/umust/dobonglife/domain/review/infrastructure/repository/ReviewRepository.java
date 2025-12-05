package com.umust.dobonglife.domain.review.infrastructure.repository;

import com.umust.dobonglife.domain.course.controller.dto.ReviewStatsDto;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.courseId =: courseId")
    List<Review> findAllByCourseId(@Param("courseId")Long courseId);

    @Query("SELECT r FROM Review r WHERE r.placeId =: placeId")
    List<Review> findAllByPlaceId(@Param("placeId")Long placeId);

    @Query("SELECT new ReviewStatsDto(SUM(r.rating), COUNT(r)) " +
            "FROM Review r WHERE r.courseId = :courseId AND r.rating IS NOT NULL")
    ReviewStatsDto getCourseReviewStats(@Param("courseId") Long courseId);

    @Query("SELECT new ReviewStatsDto(SUM(r.rating), COUNT(r)) " +
            "FROM Review r WHERE r.placeId = :placeId AND r.rating IS NOT NULL")
    ReviewStatsDto getPlaceReviewStats(@Param("placeId") Long placeId);
}
}
