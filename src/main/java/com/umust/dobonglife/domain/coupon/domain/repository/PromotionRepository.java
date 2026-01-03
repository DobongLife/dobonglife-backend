package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT c FROM Promotion c " +
            "WHERE (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    Slice<Promotion> findPromotionNoOffset(Long lastId, Pageable pageable);
}
