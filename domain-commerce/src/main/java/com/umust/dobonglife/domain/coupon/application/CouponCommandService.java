package com.umust.dobonglife.domain.coupon.application;

import com.umust.dobonglife.domain.coupon.application.port.in.ManageCouponUseCase;
import com.umust.dobonglife.domain.coupon.application.port.out.LoadCouponPort;
import com.umust.dobonglife.domain.coupon.application.port.out.SaveCouponPort;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.exception.CouponErrorCode;
import com.umust.dobonglife.domain.coupon.exception.CouponException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponCommandService implements ManageCouponUseCase {

    private final LoadCouponPort loadCouponPort;
    private final SaveCouponPort saveCouponPort;

    @Override
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
    public void cancel(Long couponId) {
        Coupon coupon = findById(couponId);
        coupon.deactivate();
    }

    @Override
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
