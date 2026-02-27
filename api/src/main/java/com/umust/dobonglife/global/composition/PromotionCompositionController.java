package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.composition.dto.response.PromotionWithBlockedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/composition/promotions")
public class PromotionCompositionController {

    private final PromotionFacade promotionFacade;

    @GetMapping
    public BaseResponse<PromotionWithBlockedResponse> getPromotionsWithBlocked(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION) int size) {
        return BaseResponse.ok(
                promotionFacade.getPromotionsWithBlocked(userId, lastId, size));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PromotionRegisterResponse>> registerPromotion(
            @RequestPart @Valid PromotionRegisterRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            Long userId) {
        return ResponseEntity.ok(BaseResponse.ok(
                promotionFacade.registerPromotion(request, userId, imageFiles)));
    }
}
