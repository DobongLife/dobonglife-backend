package com.umust.dobonglife.domain.point.controller;

import com.umust.dobonglife.domain.point.controller.dto.response.MyPointsResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointPageResponse;
import com.umust.dobonglife.domain.point.service.PointPromotionService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "포인트 API", description = "포인트 관련 API")
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {
    private final PointPromotionService pointPromotionService;
    private final PointService pointService;

    @Operation(summary = "쿠폰 첫화면 조회", description = "포인트와 프로모션(광고)를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<PointPageResponse> getMyPoint(@CurrentUserId Long userId,
                                                      @RequestParam(required = false, defaultValue = "5") Long lastId,
                                                      @RequestParam(defaultValue = "4") int size) {
        PointPageResponse response = pointPromotionService.getMyPoint(userId, lastId, size);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "나의 포인트 내역 조회", description = "포인트 내역을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/my")
    public BaseResponse<MyPointsResponse> getMyPointList(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "DESC") String order
    ) {
        return BaseResponse.ok(pointService.getPointList(userId, size, lastId, order));
    }
}
