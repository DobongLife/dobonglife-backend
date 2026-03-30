package com.umust.dobonglife.domain.point.domain.repository;

import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    void deleteAllByPointId(Long pointId);

    @Modifying
    @Query("UPDATE PointHistory ph SET ph.status = :newStatus " +
           "WHERE ph.pointId IN (SELECT p.id FROM Point p WHERE p.userId = :userId) " +
           "AND ph.status = :currentStatus")
    void updateStatusByUserId(@Param("userId") Long userId,
                              @Param("currentStatus") BaseStatus currentStatus,
                              @Param("newStatus") BaseStatus newStatus);

    @Modifying
    @Query("DELETE FROM PointHistory ph " +
           "WHERE ph.pointId IN (SELECT p.id FROM Point p WHERE p.userId = :userId) " +
           "AND ph.status = :status")
    void deleteByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BaseStatus status);
}
