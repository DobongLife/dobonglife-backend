package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionItem;
import com.umust.dobonglife.domain.user.application.UserService;
import com.umust.dobonglife.domain.user.application.dto.request.UserIsBlockedRequest;
import com.umust.dobonglife.domain.user.domain.vo.UserIsBlocked;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.PromotionWithBlockedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionFacade {

    private final PromotionService promotionService;
    private final UserService userService;

    public PromotionWithBlockedResponse getPromotionsWithBlocked(UserIsBlockedRequest request, Long lastId, int size) {
        UserIsBlocked blocked = userService.getUserIsBlocked(request);
        CursorResponse<PromotionItem> promotions = promotionService.getPromotions(lastId, size);
        return PromotionWithBlockedResponse.of(blocked.isBlocked(), promotions);
    }
}
