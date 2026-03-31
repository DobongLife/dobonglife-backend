package com.umust.dobonglife.commerce.controller;

import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotion")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<BaseResponse<CursorResponse<PromotionSummary>>> getPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION) int size) {
        return ResponseEntity.ok(BaseResponse.ok(promotionService.getPromotions(lastId, size)));
    }

    @GetMapping("/ad")
    public ResponseEntity<BaseResponse<CursorResponse<PromotionAdSummary>>> getAdPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION_AD) int size) {
        return ResponseEntity.ok(BaseResponse.ok(promotionService.getAdPromotions(lastId, size)));
    }
}
