package com.umust.dobonglife.domain.point.infrastructure.repository;

import com.umust.dobonglife.domain.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT SUM(p.amount) FROM Point p WHERE p.user.id = :userId")
    Long sumAmountByUserId(@Param("userId") Long userId);
}
