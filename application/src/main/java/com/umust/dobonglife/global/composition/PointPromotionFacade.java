package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.point.application.PointService;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.PointPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPromotionFacade {

    private final PointService pointService;
    private final PromotionService promotionService;

    public PointPageResponse getPointPage(Long userId, Long lastId, int size) {
        Long totalPoint = pointService.getUserPoint(userId);
        CursorResponse<PromotionAdSummary> promotions = promotionService.getAdPromotions(lastId, size);

        return new PointPageResponse(totalPoint, promotions);
    }
}
