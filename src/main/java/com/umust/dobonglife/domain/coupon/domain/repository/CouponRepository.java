package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.repository.custom.CouponRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {

    @Query("SELECT c FROM Coupon c " +
            "WHERE (:lastId IS NULL OR c.id < :lastId) " +
            "AND c.userId = :userId " +
            "ORDER BY c.id DESC")
    Slice<Coupon> findCouponsNoOffset(@Param("userId") Long userId,
                                      @Param("lastId") Long lastId,
                                      Pageable pageable);

    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.userId = :userId AND c.couponStatus = :couponStatus")
    int countByUserIdAndStatus(Long userId, CouponStatus couponStatus);

    @Query("SELECT c FROM Coupon c " +
            "JOIN FETCH c.promotion " +
            "WHERE c.userId = :userId AND c.id = :couponId")
    Optional<Coupon> findByUserIdAndCouponId(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
