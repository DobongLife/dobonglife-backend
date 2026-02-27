package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionItem;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionFacade {

    private final PromotionService promotionService;
    // private final UserService userService;  // TODO: user 모듈 구성 후 주입

    public PromotionWithBlockedResponse getPromotionsWithBlocked(Long userId, Long lastId, int size) {
        CursorResponse<PromotionItem> promotions = promotionService.getPromotions(lastId, size);
        // boolean blocked = userService.isBlockedUser(userId);
        boolean blocked = false; // TODO: user 모듈 연동 후 교체
        return new PromotionWithBlockedResponse(blocked, promotions);
    }

    public record PromotionWithBlockedResponse(
            boolean isBlockedUser,
            CursorResponse<PromotionItem> promotions
    ) {}
}
