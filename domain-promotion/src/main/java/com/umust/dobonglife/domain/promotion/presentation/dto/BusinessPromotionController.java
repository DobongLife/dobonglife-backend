package com.umust.dobonglife.domain.promotion.presentation.dto;

import com.umust.dobonglife.domain.promotion.application.BusinessPromotionService;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/business/promotion")
public class BusinessPromotionController {

    private final BusinessPromotionService businessPromotionService;

    public ResponseEntity<BaseResponse<PromotionRegisterResponse>> registerCoupon(
            @RequestPart @Valid PromotionRegisterRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(BaseResponse.ok(
                businessPromotionService.registerCoupon(request, userId, imageFiles)));
    }
}
