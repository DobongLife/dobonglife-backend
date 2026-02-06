package com.umust.dobonglife.domain.business.controller;

import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessPromotionResponse;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessResponse;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "사업자 API", description = "사업자 관련 API")
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @Operation(summary = "사업장 등록", description = "카테고리는 다음과 같습니다. RESTAURANT, CAFE, SHOPPING, MEDICAL_IT, BEAUTY, FITNESS, EXPERIENCE, ETC")
    @ApiResponse(
            responseCode = "200",
            description = "사업장 등록에 성공하였습니다."
    )
    @PostMapping
    public BaseResponse<Void> registerBusiness(@CurrentUserId Long userId,
                                               @RequestPart(value = "request") BusinessRequest request,
                                               @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        businessService.registerBusiness(request, userId, imageFiles);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "사업장 정보 조회", description = "사업장 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사업장 정보 조회에 성공하였습니다."
    )
    @GetMapping("/{businessId}")
    public BaseResponse<BusinessResponse> getBusiness(@PathVariable Long businessId) {
        return BaseResponse.ok(businessService.getBusinessResponse(businessId));
    }

    @Operation(summary = "사업장 정보 수정", description = "사업장 정보를 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사업장 정보 수정에 성공하였습니다."
    )
    @PostMapping("/{businessId}")
    public BaseResponse<BusinessResponse> updateBusiness(@PathVariable Long businessId,
                                             @RequestBody BusinessRequest request,
                                             @CurrentUserId Long userId) {
        return BaseResponse.ok(businessService.updateBusiness(userId, businessId, request));
    }

//    @Operation(summary = "사업장 프로모션 조회", description = "사업장 프로모션을 조회합니다.")
//    @ApiResponse(
//            responseCode = "200",
//            description = "사업장 프로모션 조회에 성공하였습니다."
//    )
//    @GetMapping("/{businessId}/promotion")
//    public BaseResponse<BusinessPromotionResponse> getBusinessPromotion(@PathVariable Long businessId,
//                                                                        @CurrentUserId Long userId) {
//        return BaseResponse.ok(businessService.getBusinessPromotion(userId, businessId));
//    }
}
