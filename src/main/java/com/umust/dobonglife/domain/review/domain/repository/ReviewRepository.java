package com.umust.dobonglife.domain.review.domain.repository;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.custom.ReviewRepositoryCustom;
import com.umust.dobonglife.domain.review.presentation.dto.response.MyReviewActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Query("SELECT r FROM Review r WHERE r.courseId =: courseId")
    List<Review> findAllByCourseId(@Param("courseId")Long courseId);

    @Query("SELECT r FROM Review r WHERE r.placeId =: placeId")
    List<Review> findAllByPlaceId(@Param("placeId")Long placeId);

    Optional<Review> findByUserId(Long userId);

    @Query("SELECT r FROM Review r " +
            "WHERE (:lastId IS NULL OR r.id < :lastId) " +
            "ORDER BY r.id DESC")
    Slice<Review> findCoursesNoOffset(Long lastId, Pageable pageable);
}
