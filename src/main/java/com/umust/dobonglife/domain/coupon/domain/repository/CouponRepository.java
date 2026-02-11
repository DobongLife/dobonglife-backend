package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.business.service.dto.CouponUsageCount;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.custom.CouponRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    List<Coupon> findAllByIssueEndDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT new com.umust.dobonglife.domain.business.service.dto.CouponUsageCount(
            c.promotion.id,
            SUM(CASE WHEN c.couponStatus = com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus.USED THEN 1 ELSE 0 END)
        )
        FROM Coupon c
        WHERE c.promotion.id IN :promotionIds
        GROUP BY c.promotion.id
    """)
    List<CouponUsageCount> countCouponUsageByPromotionIds(List<Long> promotionIds);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    UPDATE Coupon c
    SET c.couponStatus = com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus.USED
    WHERE c.id = :couponId
      AND c.userId = :userId
      AND c.couponStatus = com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus.AVAILABLE
      AND c.issueStartDate <= CURRENT_DATE
      AND c.issueEndDate >= CURRENT_DATE
    """)
    int useIfUsable(@Param("userId") Long userId, @Param("couponId") Long couponId);

    @Modifying
    @Query("DELETE FROM Coupon c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Coupon c SET c.couponStatus = :targetStatus " +
            "WHERE c.promotion IN :promotions AND c.couponStatus = :currentStatus")
    int updateStatusByPromotions(
            @Param("promotions") List<Promotion> promotions,
            @Param("currentStatus") CouponStatus currentStatus,
            @Param("targetStatus") CouponStatus targetStatus
    );

    @Modifying(clearAutomatically = true) // 삭제 후 영속성 컨텍스트 초기화
    @Transactional
    @Query("delete from Coupon c where c.promotion in :promotionList")
    void deleteAllByPromotionIn(@Param("promotionList") List<Promotion> promotionList);
}
