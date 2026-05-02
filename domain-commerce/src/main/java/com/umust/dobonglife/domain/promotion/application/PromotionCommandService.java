package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.application.port.in.ManagePromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.out.LoadPromotionPort;
import com.umust.dobonglife.domain.promotion.application.port.out.SavePromotionPort;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionUpdateRequest;
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
@Transactional
public class PromotionCommandService implements ManagePromotionUseCase {

    private final LoadPromotionPort loadPromotionPort;
    private final SavePromotionPort savePromotionPort;
    private final ImageUploader imageUploader;

    @Override
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

    @Override
    public Promotion updatePromotion(PromotionUpdateRequest request, Long promotionId, Long userId) {
        Promotion promotion = findById(promotionId);

        if (!promotion.getBusinessId().equals(userId)) {
            throw new PromotionException(PromotionErrorCode.NOT_PROMOTION_OWNER);
        }

        promotion.update(request.title(), request.description(), request.totalQuantity());
        return promotion;
    }

    @Override
    public void deductStock(Long promotionId) {
        Promotion promotion = loadPromotionPort.findByIdForUpdate(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        promotion.deductStock();
    }

    @Override
    public void restoreStock(Long promotionId) {
        Promotion promotion = loadPromotionPort.findByIdForUpdate(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        promotion.restoreStock();
    }

    @Override
    public void validateCode(Long promotionId, String code) {
        Promotion promotion = findById(promotionId);
        promotion.validateCode(code);
    }

    public Slice<Promotion> getPromotionSliceByBusinessId(Long businessId, Long lastId, int size) {
        return loadPromotionPort.findByBusinessIdNoOffset(
                businessId, lastId, PageRequest.of(0, size));
    }

    private Promotion findById(Long promotionId) {
        Promotion promotion = loadPromotionPort.findById(promotionId)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PROMOTION_NOT_FOUND));
        return promotion;
    }

    private List<String> uploadImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = imageUploader.uploadImages(imageFiles);
        if (!imageUrls.isEmpty()) {
            TransactionHelper.onRollback(() -> imageUploader.deleteImages(imageUrls));
        }
        return imageUrls;
    }
}
