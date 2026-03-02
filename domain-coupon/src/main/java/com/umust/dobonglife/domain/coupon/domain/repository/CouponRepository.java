package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
