package com.umust.dobonglife.domain.coupon.application.port.out;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface LoadCouponPort {

    Optional<Coupon> findById(Long couponId);

    Slice<Coupon> findByUserIdNoOffset(Long userId, Long lastId, Pageable pageable);

    long countByUserIdAndCouponStatus(Long userId, CouponStatus couponStatus);
}
