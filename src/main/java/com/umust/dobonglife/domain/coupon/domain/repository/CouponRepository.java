package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.repository.custom.CouponRepositoryCustom;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.CouponItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
}
