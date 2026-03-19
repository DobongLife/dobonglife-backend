package com.umust.dobonglife.domain.promotion.presentation;

import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotion")
public class PromotionController {

    private final PromotionPort promotionPort;

    @GetMapping
    public ResponseEntity<BaseResponse<CursorResponse<PromotionSummaryInfo>>> getPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION) int size) {
        return ResponseEntity.ok(BaseResponse.ok(promotionPort.getPromotions(lastId, size)));
    }

    @GetMapping("/ad")
    public ResponseEntity<BaseResponse<CursorResponse<PromotionAdInfo>>> getAdPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION_AD) int size) {
        return ResponseEntity.ok(BaseResponse.ok(promotionPort.getAdPromotions(lastId, size)));
    }
}
