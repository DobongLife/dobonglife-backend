package com.umust.dobonglife.domain.coupon.infrastructure.adapter;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.exception.CouponErrorCode;
import com.umust.dobonglife.domain.coupon.exception.CouponException;
import com.umust.dobonglife.global.port.CouponPort;
import com.umust.dobonglife.global.port.dto.PromotionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CouponPortAdapter implements CouponPort {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public Long issue(Long sagaId, Long userId, PromotionInfo promotionInfo) {
        LocalDate issueStartDate = LocalDate.now();
        LocalDate issueEndDate = issueStartDate.plusDays(promotionInfo.couponValidDays());

        Coupon coupon = Coupon.builder()
                .userId(userId)
                .promotionId(promotionInfo.promotionId())
                .couponStatus(CouponStatus.AVAILABLE)
                .issueStartDate(issueStartDate)
                .issueEndDate(issueEndDate)
                .build();

        Coupon saved = couponRepository.save(coupon);
        return saved.getId();
    }

    @Override
    @Transactional
    public void cancel(Long sagaId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponErrorCode.INVALID_COUPON_ID));
        coupon.deactivate();
    }
}
