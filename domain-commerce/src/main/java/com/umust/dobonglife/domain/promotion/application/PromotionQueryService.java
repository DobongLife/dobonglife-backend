package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.application.port.in.GetPromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.out.LoadPromotionPort;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.domain.promotion.exception.PromotionErrorCode;
import com.umust.dobonglife.domain.promotion.exception.PromotionException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionQueryService implements GetPromotionUseCase {

    private final LoadPromotionPort loadPromotionPort;

    @Override
    public CursorResponse<PromotionSummary> getPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = loadPromotionPort.findPromotionNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionSummary::from);
    }

    @Override
    public CursorResponse<PromotionAdSummary> getAdPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = loadPromotionPort.findBannerNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionAdSummary::from);
    }

    @Override
    public List<Promotion> getPromotionsByIds(List<Long> promotionIds) {
        if (promotionIds == null || promotionIds.isEmpty()) {
            return List.of();
        }
        return loadPromotionPort.findAllByIdIn(promotionIds);
    }

    @Override
    public Promotion getActivePromotion(Long promotionId) {
        Promotion promotion = findById(promotionId);
        promotion.validateActive();
        return promotion;
    }

    private Promotion findById(Long promotionId) {
        Promotion promotion = loadPromotionPort.findById(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        return promotion;
    }
}
