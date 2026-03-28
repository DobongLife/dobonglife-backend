package com.umust.dobonglife.presentation.business.controller;

import com.umust.dobonglife.application.business.service.BusinessFacade;
import com.umust.dobonglife.presentation.business.dto.response.BusinessResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사업자 API", description = "사업자 관련 API")
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessFacade businessFacade;

    @Operation(summary = "사업장 정보 조회", description = "사업장 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사업장 정보 조회에 성공하였습니다.")
    @GetMapping
    public BaseResponse<BusinessResponse> getBusiness(@CurrentUserId Long userId) {
        BusinessFacade.BusinessInfo info = businessFacade.getBusinessInfo(userId);
        return BaseResponse.ok(BusinessResponse.of(info.business(), info.place()));
    }
}
