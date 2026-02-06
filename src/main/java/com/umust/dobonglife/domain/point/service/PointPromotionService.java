package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionBannerItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.point.controller.dto.response.PointPageResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointPromotionService {
    private final PromotionService promotionService;
    private final PointService pointService;

    public PointPageResponse getMyPoint(Long userId, Long lastId, int size) {
        Long userPoint = pointService.getUserPoint(userId);
        CursorResponse<PromotionBannerItem> promotions = promotionService.getPromotionBanner(lastId, size);

        return new PointPageResponse(userPoint, promotions);
    }
}
