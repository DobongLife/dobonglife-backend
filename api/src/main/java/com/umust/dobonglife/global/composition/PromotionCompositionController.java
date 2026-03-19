package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.composition.dto.response.PromotionWithBlockedResponse;
import com.umust.dobonglife.global.port.commerce.ExchangePort;
import com.umust.dobonglife.global.port.dto.commerce.PromotionPresetInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionRegisterInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionUpdateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/composition/promotion")
public class PromotionCompositionController {

    private final PromotionFacade promotionFacade;
    private final ExchangePort exchangePort;

    @GetMapping
    public ResponseEntity<BaseResponse<PromotionWithBlockedResponse>> getPromotionsWithBlocked(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION) int size) {
        return ResponseEntity.ok(BaseResponse.ok(
                promotionFacade.getPromotionsWithBlocked(userId, lastId, size)));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<PromotionRegisterInfo>> registerPromotion(
            @RequestBody Map<String, Object> request,
            @CurrentUserId Long userId) {
        List<String> imageUrls = request.containsKey("imageUrls") ? (List<String>) request.get("imageUrls") : List.of();
        return ResponseEntity.ok(BaseResponse.ok(
                promotionFacade.registerPromotion(request, userId, imageUrls)));
    }

    @PatchMapping("/update/{promotionId}")
    public ResponseEntity<BaseResponse<PromotionUpdateInfo>> updatePromotion(
            @PathVariable Long promotionId,
            @RequestBody Map<String, Object> request,
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(BaseResponse.ok(
                promotionFacade.modifyPromotion(
                        (String) request.get("title"),
                        (String) request.get("description"),
                        Long.valueOf(request.get("totalQuantity").toString()),
                        promotionId, userId)));
    }

    @GetMapping("/preset")
    public ResponseEntity<BaseResponse<PromotionPresetInfo>> getPreset(@CurrentUserId Long userId) {
        return ResponseEntity.ok(BaseResponse.ok(promotionFacade.getPreset(userId)));
    }

    @PostMapping("/{promotionId}/exchange")
    public ResponseEntity<BaseResponse<Map<String, Object>>> exchangeCoupon(
            @PathVariable Long promotionId,
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(BaseResponse.ok(exchangePort.exchange(userId, promotionId)));
    }
}
