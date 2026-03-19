package com.umust.dobonglife.global.port.commerce;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionPresetInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionRegisterInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionUpdateInfo;

import java.util.List;
import java.util.Map;

public interface PromotionPort {
    CursorResponse<PromotionAdInfo> getAdPromotions(Long lastId, int size);
    CursorResponse<PromotionSummaryInfo> getPromotions(Long lastId, int size);
    PromotionRegisterInfo registerPromotion(Map<String, Object> request, Long userId, List<String> imageUrls);
    PromotionUpdateInfo updatePromotion(String title, String description, Long totalQuantity, Long promotionId, Long userId);
    PromotionPresetInfo getPresetByCategory(String category);
}
