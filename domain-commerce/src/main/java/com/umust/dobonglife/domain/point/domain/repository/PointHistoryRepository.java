package com.umust.dobonglife.domain.point.domain.repository;

import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Query("""
        SELECT ph FROM PointHistory ph
        WHERE ph.pointId = :pointId
        AND (:lastId IS NULL OR ph.id < :lastId)
        ORDER BY ph.id DESC
    """)
    List<PointHistory> findByPointIdDesc(@Param("pointId") Long pointId,
                                         @Param("lastId") Long lastId,
                                         Pageable pageable);

    @Query("""
        SELECT ph FROM PointHistory ph
        WHERE ph.pointId = :pointId
        AND (:lastId IS NULL OR ph.id > :lastId)
        ORDER BY ph.id ASC
    """)
    List<PointHistory> findByPointIdAsc(@Param("pointId") Long pointId,
                                        @Param("lastId") Long lastId,
                                        Pageable pageable);
}
