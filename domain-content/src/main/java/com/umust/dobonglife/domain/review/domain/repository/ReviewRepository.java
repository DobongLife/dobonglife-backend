package com.umust.dobonglife.domain.review.domain.repository;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.repository.custom.ReviewRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Modifying
    @Query("UPDATE Review r SET r.userId = null WHERE r.userId = :userId")
    void nullifyUserByUserId(@Param("userId") Long userId);
}
