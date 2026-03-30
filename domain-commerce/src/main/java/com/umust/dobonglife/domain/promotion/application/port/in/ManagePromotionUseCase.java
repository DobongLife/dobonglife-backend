package com.umust.dobonglife.domain.promotion.application.port.in;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ManagePromotionUseCase {

    Promotion createPromotion(PromotionRegisterRequest request, Long userId, List<MultipartFile> imageFiles);

    Promotion updatePromotion(PromotionUpdateRequest request, Long promotionId, Long userId);

    void deductStock(Long promotionId);

    void restoreStock(Long promotionId);

    void validateCode(Long promotionId, String code);
}
