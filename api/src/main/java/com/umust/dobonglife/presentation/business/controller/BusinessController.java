package com.umust.dobonglife.presentation.business.controller;

import com.umust.dobonglife.application.business.service.BusinessFacade;
import com.umust.dobonglife.presentation.business.dto.request.BusinessRegisterRequest;
import com.umust.dobonglife.presentation.business.dto.request.BusinessUpdateRequest;
import com.umust.dobonglife.presentation.business.dto.response.BusinessPromotionResponse;
import com.umust.dobonglife.presentation.business.dto.response.BusinessResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "사업자 API", description = "사업자 관련 API")
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessFacade businessFacade;

    @Operation(summary = "사업장 등록", description = "카테고리는 다음과 같습니다. RESTAURANT, CAFE, SHOPPING, MEDICAL_IT, BEAUTY, FITNESS, EXPERIENCE, ETC")
    @ApiResponse(responseCode = "200", description = "사업장 등록에 성공하였습니다.")
    @PostMapping
    public BaseResponse<Void> registerBusiness(@CurrentUserId Long userId,
                                               @Valid @RequestPart(value = "request") BusinessRegisterRequest request,
                                               @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        BusinessFacade.RegisterCommand command = new BusinessFacade.RegisterCommand(
                request.getPlaceId(), request.getSubName(), request.getBusinessName(),
                request.getContent(), request.getContact(), request.getEmail(),
                request.getBusinessAddress(), request.getOperatingHour(),
                request.getManagerName(), request.getBusinessNumber(),
                request.getLatitude(), request.getLongitude(),
                request.getCategory(), request.getThemes()
        );
        businessFacade.registerBusiness(command, userId, imageFiles);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "사업장 정보 조회", description = "사업장 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사업장 정보 조회에 성공하였습니다.")
    @GetMapping
    public BaseResponse<BusinessResponse> getBusiness(@CurrentUserId Long userId) {
        BusinessFacade.BusinessInfo info = businessFacade.getBusinessInfo(userId);
        return BaseResponse.ok(BusinessResponse.of(info));
    }

    @Operation(summary = "사업장 정보 수정", description = "사업장 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "사업장 정보 수정에 성공하였습니다.")
    @PatchMapping
    public BaseResponse<BusinessResponse> updateBusiness(@Valid @RequestPart(value = "request") BusinessUpdateRequest request,
                                                         @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                                         @CurrentUserId Long userId) {
        BusinessFacade.UpdateCommand command = new BusinessFacade.UpdateCommand(
                request.getSubName(), request.getBusinessName(), request.getContent(),
                request.getContact(), request.getEmail(), request.getOperatingHour(),
                request.getManagerName(), request.getCategory()
        );
        BusinessFacade.BusinessInfo info = businessFacade.updateBusiness(userId, command, imageFiles);
        return BaseResponse.ok(BusinessResponse.of(info));
    }

    @Operation(summary = "사업장 프로모션 조회", description = "사업장 프로모션을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사업장 프로모션 조회에 성공하였습니다.")
    @GetMapping("/promotion")
    public BaseResponse<CursorResponse<BusinessPromotionResponse>> getBusinessPromotion(@CurrentUserId Long userId,
                                                                                        @RequestParam(required = false) Long lastId,
                                                                                        @RequestParam(defaultValue = "3") int size) {
        BusinessFacade.PromotionPage page = businessFacade.getBusinessPromotions(userId, lastId, size);

        List<BusinessPromotionResponse> content = page.promotions().stream()
                .map(promotion -> BusinessPromotionResponse.of(
                        promotion,
                        page.usedCountMap().getOrDefault(promotion.promotionId(), 0L)
                ))
                .toList();

        return BaseResponse.ok(new CursorResponse<>(content, page.hasNext()));
    }
}
