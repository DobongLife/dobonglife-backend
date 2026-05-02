package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.dto.response.PromotionWithBlockedResponse;
import com.umust.dobonglife.global.port.BusinessPort;
import com.umust.dobonglife.global.port.commerce.PromotionPort;
import com.umust.dobonglife.global.port.dto.commerce.PromotionPresetInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionRegisterInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionUpdateInfo;
import com.umust.dobonglife.global.port.user.UserPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PromotionFacade {

    private final PromotionPort promotionPort;
    private final UserPort userPort;
    private final BusinessPort businessPort;

    @CircuitBreaker(name = "user-service")
    public PromotionWithBlockedResponse getPromotionsWithBlocked(Long userId, Long lastId, int size) {
        boolean blocked = userPort.isBlockedUser(userId);
        CursorResponse<PromotionSummaryInfo> promotions = promotionPort.getPromotions(lastId, size);
        return new PromotionWithBlockedResponse(blocked, promotions);
    }

    @CircuitBreaker(name = "commerce-service")
    public PromotionRegisterInfo registerPromotion(Map<String, Object> request, Long userId, List<String> imageUrls) {
        businessPort.checkBusiness(userId);
        return promotionPort.registerPromotion(request, userId, imageUrls);
    }

    @CircuitBreaker(name = "commerce-service")
    public PromotionUpdateInfo modifyPromotion(String title, String description, Long totalQuantity, Long promotionId, Long userId) {
        businessPort.checkBusiness(userId);
        return promotionPort.updatePromotion(title, description, totalQuantity, promotionId, userId);
    }

    @CircuitBreaker(name = "commerce-service")
    public PromotionPresetInfo getPreset(Long userId) {
        Category category = businessPort.getBusinessCategory(userId);
        return promotionPort.getPresetByCategory(category.name());
    }
}
