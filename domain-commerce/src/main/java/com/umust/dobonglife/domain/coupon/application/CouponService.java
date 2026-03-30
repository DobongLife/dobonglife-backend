package com.umust.dobonglife.domain.coupon.application;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.port.in.GetCouponUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.ManageCouponUseCase;
import com.umust.dobonglife.domain.coupon.application.port.out.LoadCouponPort;
import com.umust.dobonglife.domain.coupon.application.port.out.SaveCouponPort;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.domain.coupon.exception.CouponErrorCode;
import com.umust.dobonglife.domain.coupon.exception.CouponException;
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
public class CouponService implements GetCouponUseCase, ManageCouponUseCase {

    private final LoadCouponPort loadCouponPort;
    private final SaveCouponPort saveCouponPort;

    @Override
    public CursorResponse<CouponDetail> getMyCoupons(Long userId, Long lastId, int size) {
        Slice<Coupon> coupons = loadCouponPort.findByUserIdNoOffset(
                userId, lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(coupons, CouponDetail::from);
    }

    @Override
    public MyCouponStatus getMyCouponStatus(Long userId) {
        Long available = loadCouponPort.countByUserIdAndCouponStatus(userId, CouponStatus.AVAILABLE);
        Long used = loadCouponPort.countByUserIdAndCouponStatus(userId, CouponStatus.USED);
        Long expired = loadCouponPort.countByUserIdAndCouponStatus(userId, CouponStatus.EXPIRED);
        return MyCouponStatus.of(available, used, expired);
    }

    @Override
    @Transactional
    public Long issue(Long userId, Long promotionId, int couponValidDays) {
        Coupon coupon = Coupon.builder()
                .userId(userId)
                .promotionId(promotionId)
                .couponStatus(CouponStatus.AVAILABLE)
                .issueStartDate(LocalDate.now())
                .issueEndDate(LocalDate.now().plusDays(couponValidDays))
                .build();
        return saveCouponPort.save(coupon).getId();
    }

    @Override
    @Transactional
    public void cancel(Long couponId) {
        Coupon coupon = findById(couponId);
        coupon.deactivate();
    }

    @Override
    @Transactional
    public void useCoupon(Long couponId, Long userId) {
        Coupon coupon = findById(couponId);
        if (!coupon.getUserId().equals(userId)) {
            throw new CouponException(CouponErrorCode.INVALID_COUPON_ID);
        }
        coupon.used();
        saveCouponPort.save(coupon);
    }

    private Coupon findById(Long couponId) {
        return loadCouponPort.findById(couponId).orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON_ID));
    }
}
