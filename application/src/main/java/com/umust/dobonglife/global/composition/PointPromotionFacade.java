package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.PointPageResponse;
import com.umust.dobonglife.global.port.commerce.PointPort;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPromotionFacade {

    private final PointPort pointPort;
    private final PromotionPort promotionPort;

    public PointPageResponse getPointPage(Long userId, Long lastId, int size) {
        Long totalPoint = pointPort.getUserPoint(userId);
        CursorResponse<PromotionAdInfo> promotions = promotionPort.getAdPromotions(lastId, size);

        return new PointPageResponse(totalPoint, promotions);
    }
}
