package com.umust.dobonglife.domain.promotion.presentation.dto;

import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionBannerItem;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionGetResponse;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionItem;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponseEntity<BaseResponse<PromotionGetResponse>> getPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size) {
        CursorResponse<PromotionItem> promotions = promotionService.getPromotions(lastId, size);
        return ResponseEntity.ok(BaseResponse.ok(new PromotionGetResponse(promotions)));
    }

    @GetMapping("/{promotionId}")
    public BaseResponse<PromotionItem> getPromotion(@PathVariable Long promotionId) {
        return BaseResponse.ok(PromotionItem.from(promotionService.getPromotion(promotionId)));
    }

    @GetMapping("/banners")
    public BaseResponse<CursorResponse<PromotionBannerItem>> getBanners(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "5") int size) {
        return BaseResponse.ok(promotionService.getBanners(lastId, size));
    }
}
