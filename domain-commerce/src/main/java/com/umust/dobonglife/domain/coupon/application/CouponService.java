package com.umust.dobonglife.domain.coupon.application;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponCleanupUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponRestoreUseCase;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.application.port.out.LoadCouponPort;
import com.umust.dobonglife.domain.coupon.application.port.out.SaveCouponPort;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService implements CouponCleanupUseCase, CouponRestoreUseCase {

    private final LoadCouponPort loadCouponPort;
    private final SaveCouponPort saveCouponPort;

    public CursorResponse<CouponDetail> getMyCoupons(Long userId, Long lastId, int size) {
        Slice<Coupon> coupons = loadCouponPort.findByUserIdNoOffset(
                userId, lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(coupons, CouponDetail::from);
    }

    public MyCouponStatus getMyCouponStatus(Long userId) {
        Long available = loadCouponPort.countByUserIdAndCouponStatus(userId, CouponStatus.AVAILABLE);
        Long used = loadCouponPort.countByUserIdAndCouponStatus(userId, CouponStatus.USED);
        Long expired = loadCouponPort.countByUserIdAndCouponStatus(userId, CouponStatus.EXPIRED);
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
        return saveCouponPort.save(coupon).getId();
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
        saveCouponPort.save(coupon);
    }

    public Map<Long, Long> getUsedCountByPromotionIds(List<Long> promotionIds) {
        return loadCouponPort.countByPromotionIdsAndStatus(promotionIds, CouponStatus.USED)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    @Transactional
    public void markPendingByUserId(Long userId) {
        saveCouponPort.updateStatusByUserId(userId, BaseStatus.ACTIVE, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void finalizeByUserId(Long userId) {
        saveCouponPort.deleteByUserIdAndStatus(userId, BaseStatus.PENDING);
    }

    @Override
    @Transactional
    public void restoreByUserId(Long userId) {
        saveCouponPort.updateStatusByUserId(userId, BaseStatus.PENDING, BaseStatus.ACTIVE);
    }

    private Coupon findById(Long couponId) {
        return loadCouponPort.findById(couponId).orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON_ID));
    }
}
