package com.umust.dobonglife.domain.business.controller;

import com.umust.dobonglife.domain.business.controller.dto.request.BusinessNumberRequest;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import com.umust.dobonglife.domain.business.service.BusinessService;
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

    @Operation(summary = "사업장 등록", description = "카테고리는 다음과 같습니다. RESTAURANT, CAFE, SHOPPING, CULTURE, EDUCATION, MEDICAL, BEAUTY, FITNESS, EXPERIENCE, ETC")
    @ApiResponse(
            responseCode = "200",
            description = "사업장 등록에 성공하였습니다."
    )
    @PostMapping
    public BaseResponse<Void> registerBusiness(@CurrentUserId Long userId,
                                               @RequestPart(value = "request") BusinessRequest request,
                                               @RequestPart(value = "imageFiles", required = true) List<MultipartFile> imageFiles) {
        businessService.registerBusiness(request, userId, imageFiles);
        return BaseResponse.ok(null);
    }
}
