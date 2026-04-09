package com.umust.dobonglife.domain.coupon.application;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponCleanupUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponRestoreUseCase;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.domain.coupon.exception.CouponErrorCode;
import com.umust.dobonglife.domain.coupon.exception.CouponException;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService implements CouponCleanupUseCase, CouponRestoreUseCase {

    private final CouponRepository couponRepository;

    public CursorResponse<CouponDetail> getMyCoupons(Long userId, Long lastId, int size) {
        Slice<Coupon> coupons = couponRepository.findByUserIdNoOffset(
                userId, lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(coupons, CouponDetail::from);
    }

    public MyCouponStatus getMyCouponStatus(Long userId) {
        Long available = couponRepository.countByUserIdAndCouponStatus(userId, CouponStatus.AVAILABLE);
        Long used = couponRepository.countByUserIdAndCouponStatus(userId, CouponStatus.USED);
        Long expired = couponRepository.countByUserIdAndCouponStatus(userId, CouponStatus.EXPIRED);
        return MyCouponStatus.of(available, used, expired);
    }

    @Transactional
    public Long issue(Long userId, Long promotionId, int couponValidDays) {
        Coupon coupon = Coupon.builder()
                .userId(userId)
                .promotionId(promotionId)
                .couponStatus(CouponStatus.AVAILABLE)
                .issueStartDate(LocalDate.now())
                .issueEndDate(LocalDate.now().plusDays(couponValidDays))
                .build();
        return couponRepository.save(coupon).getId();
    }

    @Transactional
    public void cancel(Long couponId) {
        Coupon coupon = findById(couponId);
        coupon.deactivate();
    }

    @Transactional
    public void useCoupon(Long couponId, Long userId) {
        Coupon coupon = findById(couponId);
        if (!coupon.getUserId().equals(userId)) {
            throw new CouponException(CouponErrorCode.INVALID_COUPON_ID);
        }
        coupon.used();
        couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public void markPendingByUserId(Long userId) {
        couponRepository.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void finalizeByUserId(Long userId) {
        couponRepository.deleteByUserIdAndStatus(userId, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void restoreByUserId(Long userId) {
        couponRepository.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }

    private Coupon findById(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON_ID));
    }
}
