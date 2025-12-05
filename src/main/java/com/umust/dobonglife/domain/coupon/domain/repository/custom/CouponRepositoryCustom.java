package com.umust.dobonglife.domain.coupon.domain.repository.custom;

import com.umust.dobonglife.domain.coupon.presentation.dto.response.CouponItem;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.MyCouponStatus;

import java.util.List;

public interface CouponRepositoryCustom {
    List<CouponItem> getCouponItemList(Long userId);
    MyCouponStatus getMyCouponStatus(Long userId);

}
