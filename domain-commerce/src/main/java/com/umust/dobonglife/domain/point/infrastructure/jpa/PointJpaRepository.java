package com.umust.dobonglife.domain.point.infrastructure.jpa;

import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.global.common.model.BaseStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Point p WHERE p.userId = :userId")
    Optional<Point> findByUserIdForUpdate(@Param("userId") Long userId);

    void deleteByUserId(Long userId);

    @Modifying
    @Query("UPDATE Point p SET p.status = :newStatus WHERE p.userId = :userId AND p.status = :currentStatus")
    void updateStatusByUserId(@Param("userId") Long userId,
                              @Param("currentStatus") BaseStatus currentStatus,
                              @Param("newStatus") BaseStatus newStatus);

    @Modifying
    @Query("DELETE FROM Point p WHERE p.userId = :userId AND p.status = :status")
    void deleteByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BaseStatus status);
}
