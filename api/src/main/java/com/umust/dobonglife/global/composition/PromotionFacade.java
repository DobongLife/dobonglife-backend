package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.domain.business.application.dto.BusinessService;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.domain.user.application.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.PromotionWithBlockedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionFacade {

    private final PromotionService promotionService;
    private final UserService userService;
    private final BusinessService businessService;

    public PromotionWithBlockedResponse getPromotionsWithBlocked(Long userId, Long lastId, int size) {
        boolean blocked = userService.isUserBlocked(userId);
        CursorResponse<PromotionSummary> promotions = promotionService.getPromotions(lastId, size);
        return PromotionWithBlockedResponse.of(blocked, promotions);
    }

    public PromotionRegisterResponse registerPromotion(PromotionRegisterRequest request, Long userId, List<MultipartFile> imageFiles) {
        businessService.checkBusiness(userId);
        Promotion promotion = promotionService.createPromotion(request, userId, imageFiles);
        return PromotionRegisterResponse.from(promotion);
    }
}
