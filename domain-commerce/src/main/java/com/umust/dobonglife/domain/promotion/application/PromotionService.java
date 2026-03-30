package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.application.port.in.GetPromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.in.ManagePromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.out.LoadPromotionPort;
import com.umust.dobonglife.domain.promotion.application.port.out.SavePromotionPort;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.domain.promotion.exception.PromotionErrorCode;
import com.umust.dobonglife.domain.promotion.exception.PromotionException;
import com.umust.dobonglife.global.common.image.ImageUploader;
import com.umust.dobonglife.global.common.transaction.TransactionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionService implements GetPromotionUseCase, ManagePromotionUseCase {

    private final LoadPromotionPort loadPromotionPort;
    private final SavePromotionPort savePromotionPort;
    private final ImageUploader imageUploader;

    @Override
    public CursorResponse<PromotionSummary> getPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = loadPromotionPort.findPromotionNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionSummary::from);
    }

    @Override
    public CursorResponse<PromotionAdSummary> getAdPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = loadPromotionPort.findBannerNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionAdSummary::from);
    }

    @Override
    @Transactional
    public Promotion createPromotion(PromotionRegisterRequest request, Long userId, List<MultipartFile> imageFiles) {
        List<String> imageUrls = uploadImages(imageFiles);

        Promotion promotion = Promotion.builder()
                .businessId(userId)
                .category(request.category())
                .title(request.title())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .couponValidDays(request.validPeriod())
                .discountType(request.discountType())
                .discountValue(request.discountValue())
                .minPrice(request.minPrice())
                .maxPrice(request.maxPrice())
                .point(request.point())
                .totalQuantity(request.totalQuantity())
                .build();

        promotion.attachImages(imageUrls);

        return savePromotionPort.save(promotion);
    }

    private List<String> uploadImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = imageUploader.uploadImages(imageFiles);
        if (!imageUrls.isEmpty()) {
            TransactionHelper.onRollback(() -> imageUploader.deleteImages(imageUrls));
        }
        return imageUrls;
    }

    @Override
    @Transactional
    public Promotion updatePromotion(PromotionUpdateRequest request, Long promotionId, Long userId) {
        Promotion promotion = findById(promotionId);

        if (!promotion.getBusinessId().equals(userId)) {
            throw new PromotionException(PromotionErrorCode.NOT_PROMOTION_OWNER);
        }

        promotion.update(request.title(), request.description(), request.totalQuantity());
        return promotion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getPromotionsByIds(List<Long> promotionIds) {
        if (promotionIds == null || promotionIds.isEmpty()) {
            return List.of();
        }
        return loadPromotionPort.findAllByIdIn(promotionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Promotion getActivePromotion(Long promotionId) {
        Promotion promotion = findById(promotionId);
        promotion.validateActive();
        return promotion;
    }

    @Override
    @Transactional
    public void deductStock(Long promotionId) {
        Promotion promotion = loadPromotionPort.findByIdForUpdate(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        promotion.deductStock();
    }

    @Override
    @Transactional
    public void restoreStock(Long promotionId) {
        Promotion promotion = loadPromotionPort.findByIdForUpdate(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        promotion.restoreStock();
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCode(Long promotionId, String code) {
        Promotion promotion = findById(promotionId);
        promotion.validateCode(code);
    }

    private Promotion findById(Long promotionId) {
        Promotion promotion = loadPromotionPort.findById(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        return promotion;
    }
}
