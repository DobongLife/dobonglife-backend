package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로모션 API", description = "프로모션 관련 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotion")
public class PromotionController {
    private final PromotionService promotionService;

    @Operation(summary = "프로모션 조회", description = "프로모션을 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<CursorResponse<PromotionItem>> getPromotion(@RequestParam(required = false) Long lastId,
                                                        @RequestParam(defaultValue = "2") int size){
        CursorResponse<PromotionItem> response = promotionService.getPromotion(lastId, size);
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
}
