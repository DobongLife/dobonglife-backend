package com.umust.dobonglife.domain.home.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.home.controller.dto.response.HomeSummaryResponse;
import com.umust.dobonglife.domain.home.service.HomeService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/home")
@Tag(name = "홈 API", description = "홈 관련 API")
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 조회 (홈메인)", description = "홈 view")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<HomeSummaryResponse> viewHome(@RequestParam(required = false, defaultValue = "10") Long lastId,
                                                                @RequestParam(defaultValue = "3") int size){
        HomeSummaryResponse response = homeService.getHomeSummary(lastId, size);
        return BaseResponse.ok(response);
    }
}
