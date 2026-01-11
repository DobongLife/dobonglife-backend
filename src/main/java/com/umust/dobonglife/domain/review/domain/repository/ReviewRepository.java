package com.umust.dobonglife.domain.review.domain.repository;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.custom.ReviewRepositoryCustom;
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

    @Query("SELECT r FROM Review r " +
            "WHERE (:lastId IS NULL OR r.id < :lastId) " +
            "ORDER BY r.id DESC")
    Slice<Review> findReviewsNoOffset(Long lastId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.user " +
            "WHERE (:lastId IS NULL OR r.id < :lastId) " +
            "AND r.user.id = :userId " +
            "ORDER BY r.id DESC")
    Slice<Review> findMyReviewsNoOffset(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    int countByUserId(Long userId);

    @Query("SELECT r FROM Review r " +
            "WHERE r.courseId = :courseId " +
            "AND (:lastId IS NULL OR r.id < :lastId) " +
            "ORDER BY r.id DESC")
    Slice<Review> findCourseReviewsNoOffset(Long courseId, Long lastId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "WHERE r.placeId = :placeId " +
            "AND (:lastId IS NULL OR r.id < :lastId) " +
            "ORDER BY r.id DESC")
    Slice<Review> findPlaceReviewsNoOffset(Long placeId, Long lastId, Pageable pageable);
}
