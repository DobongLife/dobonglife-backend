package com.umust.dobonglife.domain.review.infrastructure.repository;

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
}
