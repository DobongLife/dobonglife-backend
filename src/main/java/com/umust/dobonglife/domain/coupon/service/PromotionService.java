package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.*;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    private final PointService pointService;

    public PromotionResponse getPromotion(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Long point = pointService.getUserPoint(userId);
        Slice<Promotion> promotions = promotionRepository.findPromotionNoOffset(lastId, pageable);

        return new PromotionResponse(point, convertToCursorResponse(promotions));
    }

    private CursorResponse<PromotionItem> convertToCursorResponse(Slice<Promotion> promotions) {
        List<PromotionItem> content = promotions.getContent().stream()
                .map(PromotionItem::from)
                .toList();
        return new CursorResponse<>(content, promotions.hasNext());
    }
}
