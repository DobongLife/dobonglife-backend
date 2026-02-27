package com.umust.dobonglife.domain.promotion.domain.repository;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
        SELECT p FROM Promotion p
        WHERE (:lastId IS NULL OR p.id < :lastId)
        ORDER BY p.id DESC
    """)
    Slice<Promotion> findPromotionNoOffset(@Param("lastId") Long lastId, Pageable pageable);

    @Query("""
        SELECT p FROM Promotion p
        WHERE p.priority IS NOT NULL
        AND (:lastId IS NULL OR p.id < :lastId)
        ORDER BY p.priority ASC, p.id DESC
    """)
    Slice<Promotion> findBannerNoOffset(@Param("lastId") Long lastId, Pageable pageable);
}
