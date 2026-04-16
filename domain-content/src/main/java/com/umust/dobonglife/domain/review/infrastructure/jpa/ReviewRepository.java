package com.umust.dobonglife.domain.review.infrastructure.jpa;

import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.infrastructure.jpa.custom.ReviewRepositoryCustom;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    @Modifying
    @Query("UPDATE Review r SET r.userId = null WHERE r.userId = :userId")
    void nullifyUserByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Review r SET r.status = :newStatus WHERE r.userId = :userId AND r.status = :currentStatus")
    void updateStatusByUserId(@Param("userId") Long userId,
                              @Param("currentStatus") BaseStatus currentStatus,
                              @Param("newStatus") BaseStatus newStatus);

    @Modifying
    @Query("UPDATE Review r SET r.userId = null, r.status = :newStatus WHERE r.userId = :userId AND r.status = :currentStatus")
    void nullifyUserAndUpdateStatus(@Param("userId") Long userId,
                                    @Param("currentStatus") BaseStatus currentStatus,
                                    @Param("newStatus") BaseStatus newStatus);
}
