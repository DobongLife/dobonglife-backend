package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
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

    void deleteAllByUserId(Long userId);

    @Modifying
    @Query("UPDATE Coupon c SET c.status = :newStatus WHERE c.userId = :userId AND c.status = :currentStatus")
    void updateStatusByUserId(@Param("userId") Long userId,
                              @Param("currentStatus") BaseStatus currentStatus,
                              @Param("newStatus") BaseStatus newStatus);

    @Modifying
    @Query("DELETE FROM Coupon c WHERE c.userId = :userId AND c.status = :status")
    void deleteByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BaseStatus status);
}
