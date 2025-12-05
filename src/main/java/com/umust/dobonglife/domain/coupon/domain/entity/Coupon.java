package com.umust.dobonglife.domain.coupon.domain.entity;

import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "promotion_name")
    private String name;

    @Column(name = "issue_start_date", nullable = false)
    private LocalDate issueStartDate;

    @Column(name = "issue_end_date", nullable = false)
    private LocalDate issueEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_status", nullable = false)
    private CouponStatus couponStatus = CouponStatus.AVAILABLE;

    public Coupon(Long userId, Long promotionId, String name, LocalDate issueStartDate, LocalDate issueEndDate, CouponStatus couponStatus) {
        this.userId = userId;
        this.promotionId = promotionId;
        this.name = name;
        this.issueStartDate = issueStartDate;
        this.issueEndDate = issueEndDate;
        this.couponStatus = couponStatus;
    }

    public void updateCouponStatus() {
        couponStatus = CouponStatus.USED;
    }
}
