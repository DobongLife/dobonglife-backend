package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT p FROM Promotion p " +
            "JOIN FETCH p.place " +
            "WHERE (:lastId IS NULL OR p.id < :lastId) " +
            "ORDER BY p.id DESC")
    Slice<Promotion> findPromotionWithPlaceNoOffset(Long lastId, Pageable pageable);

    @Query("""
        SELECT p FROM Promotion p
        WHERE p.businessesId = :userId
          AND (:lastId IS NULL OR p.id < :lastId)
        ORDER BY p.id DESC
    """)
    Slice<Promotion> findPromotionNoOffsetByUserId(
            Long userId,
            Long lastId,
            Pageable pageable
    );

    @Modifying
    @Query("""
        UPDATE Promotion p
        SET p.issuedCount = p.issuedCount + 1
        WHERE p.id = :promotionId
          AND p.issuedCount < p.totalQuantity
          AND p.startDate <= :today
          AND p.endDate >= :today
    """)
    int tryIssueCoupon(@Param("promotionId") Long promotionId,
                       @Param("today") LocalDate today);
}
