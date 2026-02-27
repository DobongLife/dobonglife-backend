package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
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
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final ImageUploader imageUploader;

    public CursorResponse<PromotionSummary> getPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = promotionRepository.findPromotionNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionSummary::from);
    }

    public CursorResponse<PromotionAdSummary> getAdPromotions(Long lastId, int size) {
        Slice<Promotion> promotions = promotionRepository.findBannerNoOffset(
                lastId, PageRequest.of(0, size));
        return CursorUtils.toCursorResponse(promotions, PromotionAdSummary::from);
    }

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

        return promotionRepository.save(promotion);
    }

    private List<String> uploadImages(List<MultipartFile> imageFiles) {
        List<String> imageUrls = imageUploader.uploadImages(imageFiles);
        if (!imageUrls.isEmpty()) {
            TransactionHelper.onRollback(() -> imageUploader.deleteImages(imageUrls));
        }
        return imageUrls;
    }
}
