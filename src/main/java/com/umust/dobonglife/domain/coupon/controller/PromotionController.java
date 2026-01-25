package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.*;
import com.umust.dobonglife.domain.coupon.service.PreSetService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "프로모션 API", description = "프로모션 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotion")
public class PromotionController {
    private final PromotionService promotionService;
    private final PreSetService preSetService;

    @Operation(summary = "프로모션 조회", description = "프로모션을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<PromotionGetResponse> getPromotion(@CurrentUserId Long userId,
                                       @RequestParam(required = false) Long lastId,
                                       @RequestParam(defaultValue = "2") int size){
        PromotionGetResponse response = promotionService.getPromotionWithBlocked(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "쿠폰 발급(포인트 교환)", description = "포인트로 쿠폰을 발급합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PostMapping("/{promotionId}")
    public BaseResponse<UsedCouponResponse> changePointToCoupon(@CurrentUserId Long userId,
                                                                @PathVariable(name = "promotionId") Long promotionId){
        UsedCouponResponse response = promotionService.changePointToCoupon(userId, promotionId);
        return BaseResponse.ok(response);
    }

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

    @Operation(summary = "프리셋 조회하기", description = "프리셋을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/preset")
    public BaseResponse<List<PresetResponse>> getPreset() {
        List<PresetResponse> responses = preSetService.getAllPresets();
        return BaseResponse.ok(responses);
    }
}
