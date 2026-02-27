package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.user.application.dto.request.UserIsBlockedRequest;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.composition.dto.response.PromotionWithBlockedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/composition/promotions")
public class PromotionCompositionController {

    private final PromotionFacade promotionFacade;

    @GetMapping
    public BaseResponse<PromotionWithBlockedResponse> getPromotionsWithBlocked(
            @CurrentUserId UserIsBlockedRequest request,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size) {
        return BaseResponse.ok(
                promotionFacade.getPromotionsWithBlocked(
                        request, lastId, size));
    }
}
