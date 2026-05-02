package com.umust.dobonglife.domain.coupon.infrastructure.adapter;

import com.umust.dobonglife.domain.coupon.application.port.out.LoadCouponPort;
import com.umust.dobonglife.domain.coupon.application.port.out.SaveCouponPort;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.infrastructure.jpa.CouponJpaRepository;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponPersistenceAdapter implements LoadCouponPort, SaveCouponPort {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public Slice<Coupon> findByUserIdNoOffset(Long userId, Long lastId, Pageable pageable) {
        return couponJpaRepository.findByUserIdNoOffset(userId, lastId, pageable);
    }

    @Override
    public long countByUserIdAndCouponStatus(Long userId, CouponStatus couponStatus) {
        return couponJpaRepository.countByUserIdAndCouponStatus(userId, couponStatus);
    }

    @Override
    public List<Object[]> countByPromotionIdsAndStatus(List<Long> promotionIds, CouponStatus status) {
        return couponJpaRepository.countByPromotionIdsAndStatus(promotionIds, status);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public void updateStatusByUserId(Long userId, BaseStatus currentStatus, BaseStatus newStatus) {
        couponJpaRepository.updateStatusByUserId(userId, currentStatus, newStatus);
    }

    @Override
    public void deleteByUserIdAndStatus(Long userId, BaseStatus status) {
        couponJpaRepository.deleteByUserIdAndStatus(userId, status);
    }
}
