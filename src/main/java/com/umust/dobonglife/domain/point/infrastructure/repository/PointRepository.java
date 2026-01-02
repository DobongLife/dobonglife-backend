package com.umust.dobonglife.domain.point.infrastructure.repository;

import com.umust.dobonglife.domain.point.domain.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT SUM(p.amount) FROM Point p WHERE p.user.id = :userId")
    Long sumAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user.id = :userId AND p.amount > 0")
    Long sumPositiveAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Point p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Point> findTopNByUserId(@Param("userId") Long userId, Pageable pageable);
}
