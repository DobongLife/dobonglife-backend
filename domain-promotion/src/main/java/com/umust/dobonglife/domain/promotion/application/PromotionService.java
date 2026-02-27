package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.domain.repository.PromotionRepository;
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
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public CursorResponse<PromotionSummary> getPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = promotionRepository.findPromotionNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionSummary::from);
    }

    public CursorResponse<PromotionAdSummary> getBanners(Long lastId, int size) {
        Slice<Promotion> promotions = promotionRepository.findBannerNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionAdSummary::from);
    }
}
