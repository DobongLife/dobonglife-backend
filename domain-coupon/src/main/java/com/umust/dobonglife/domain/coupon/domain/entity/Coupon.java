package com.umust.dobonglife.domain.coupon.domain.entity;

import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long promotionId;

    private CouponStatus status;

    private LocalDate issueStartDate;

    private LocalDate issueEndDate;

    @Builder
    public Coupon(Long userId, Long promotionId, CouponStatus status, LocalDate issueStartDate, LocalDate issueEndDate) {
        this.userId = userId;
        this.promotionId = promotionId;
        this.status = status;
        this.issueStartDate = issueStartDate;
        this.issueEndDate = issueEndDate;
    }
}
