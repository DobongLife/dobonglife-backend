package com.umust.dobonglife.domain.promotion.application.port.in;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;

public interface GetPromotionUseCase {

    CursorResponse<PromotionSummary> getPromotions(Long lastId, int size);

    CursorResponse<PromotionAdSummary> getAdPromotions(Long lastId, int size);

    Promotion getActivePromotion(Long promotionId);

    List<Promotion> getPromotionsByIds(List<Long> promotionIds);
}
