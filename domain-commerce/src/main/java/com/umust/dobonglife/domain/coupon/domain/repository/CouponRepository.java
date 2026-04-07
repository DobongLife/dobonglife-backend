package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("""
        SELECT c FROM Coupon c
        WHERE c.userId = :userId
        AND (:lastId IS NULL OR c.id < :lastId)
        ORDER BY c.id DESC
    """)
    Slice<Coupon> findByUserIdNoOffset(@Param("userId") Long userId,
                                       @Param("lastId") Long lastId,
                                       Pageable pageable);

    long countByUserIdAndCouponStatus(Long userId, CouponStatus couponStatus);

    @Query("""
        SELECT c.promotionId, COUNT(c)
        FROM Coupon c
        WHERE c.promotionId IN :promotionIds AND c.couponStatus = :status
        GROUP BY c.promotionId
    """)
    List<Object[]> countByPromotionIdsAndStatus(@Param("promotionIds") List<Long> promotionIds,
                                                @Param("status") CouponStatus status);
}
