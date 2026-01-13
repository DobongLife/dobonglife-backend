package com.umust.dobonglife.domain.point.domain.repository;

import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.repository.custom.PointRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {
    @Query("SELECT SUM(p.amount) FROM Point p WHERE p.user.id = :userId")
    Long sumAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user.id = :userId AND p.amount > 0")
    Long sumPositiveAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Point p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Point> findTopNByUserId(@Param("userId") Long userId, Pageable pageable);

    // UPDATE 실행 전에 변경사항 flush하고, 실행 후에는 DB와 상태를 강제로 동기화
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE Point p
    SET p.isUsed = true,
        p.afterBalance = :afterBalance
    WHERE p.id = :pointId
      AND p.user.id = :userId
      AND p.isUsed = false
""")
    int markUsedWithAfterBalance(@Param("userId") Long userId,
                                 @Param("pointId") Long pointId,
                                 @Param("afterBalance") long afterBalance);
}
