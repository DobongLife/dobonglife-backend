package com.umust.dobonglife.commerce.facade;

import com.umust.dobonglife.commerce.client.UserServiceClient;
import com.umust.dobonglife.domain.promotion.application.PresetService;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionPresetResponse;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionUpdateResponse;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.port.BusinessPort;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionFacade {

    private final PromotionService promotionService;
    private final PresetService presetService;
    private final UserServiceClient userServiceClient;
    private final BusinessPort businessPort;

    public PromotionWithBlockedResponse getPromotionsWithBlocked(Long userId, Long lastId, int size) {
        boolean blocked = userServiceClient.isBlockedUser(userId);
        CursorResponse<PromotionSummary> promotions = promotionService.getPromotions(lastId, size);
        return new PromotionWithBlockedResponse(blocked, promotions);
    }

    public PromotionRegisterResponse registerPromotion(PromotionRegisterRequest request, Long userId, List<MultipartFile> imageFiles) {
        businessPort.checkBusiness(userId);
        Promotion promotion = promotionService.createPromotion(request, userId, imageFiles);
        return PromotionRegisterResponse.from(promotion);
    }

    public PromotionUpdateResponse modifyPromotion(PromotionUpdateRequest request, Long promotionId, Long userId) {
        businessPort.checkBusiness(userId);
        Promotion promotion = promotionService.updatePromotion(request, promotionId, userId);
        return PromotionUpdateResponse.from(promotion);
    }

    public PromotionPresetResponse getPreset(Long userId) {
        Category category = businessPort.getBusinessCategory(userId);
        Preset preset = presetService.getPresetByCategory(category);
        return PromotionPresetResponse.from(preset);
    }

    public record PromotionWithBlockedResponse(boolean blocked, CursorResponse<PromotionSummary> promotions) {}
}
