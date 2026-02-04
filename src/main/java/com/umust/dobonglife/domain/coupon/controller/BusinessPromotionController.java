package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PresetResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionUpdateResponse;
import com.umust.dobonglife.domain.coupon.service.PreSetService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "비즈니스 쿠폰 API", description = "비즈니스 쿠폰 API")
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessPromotionController {

    private final PromotionService promotionService;
    private final PreSetService preSetService;

    @Operation(summary = "쿠폰 등록하기", description = "쿠폰을 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<PromotionRegisterResponse> registerCoupon(
            @RequestPart @Valid PromotionRegisterRequest request,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @CurrentUserId Long userId) {
        PromotionRegisterResponse responses = promotionService.registerCoupon(request, userId, imageFiles);
        return BaseResponse.ok(responses);
    }

    @Operation(summary = "쿠폰 수정하기", description = "쿠폰을 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PatchMapping("/update/{promotionId}")
    public BaseResponse<PromotionUpdateResponse> updateCoupon(
            @PathVariable Long promotionId,
            @RequestBody @Valid PromotionUpdateRequest request,
            @CurrentUserId Long userId) {
        PromotionUpdateResponse responses = promotionService.updateCoupon(request, promotionId, userId);
        return BaseResponse.ok(responses);
    }

    @Operation(summary = "프리셋 조회하기", description = "프리셋을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/preset")
    public BaseResponse<PresetResponse> getPreset(@CurrentUserId Long userId) {
        PresetResponse responses = preSetService.getPreset(userId);
        return BaseResponse.ok(responses);
    }
}
