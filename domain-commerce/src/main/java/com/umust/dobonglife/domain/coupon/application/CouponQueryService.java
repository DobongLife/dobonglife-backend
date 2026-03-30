package com.umust.dobonglife.domain.coupon.application;

import com.umust.dobonglife.domain.coupon.application.dto.CouponDetail;
import com.umust.dobonglife.domain.coupon.application.port.in.GetCouponUseCase;
import com.umust.dobonglife.domain.coupon.application.port.out.LoadCouponPort;
import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.vo.CouponStatus;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponQueryService implements GetCouponUseCase {

    private final LoadCouponPort loadCouponPort;

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
}
