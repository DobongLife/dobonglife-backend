package com.umust.dobonglife.domain.promotion.presentation;

import com.umust.dobonglife.domain.promotion.application.PresetService;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.domain.entity.PromotionImage;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.dto.commerce.PromotionAdInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionPresetInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionRegisterInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionUpdateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/promotion")
@RequiredArgsConstructor
public class InternalPromotionController {

    private final PromotionService promotionService;
    private final PresetService presetService;

    @GetMapping("/ad")
    public CursorResponse<PromotionAdInfo> getAdPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        CursorResponse<PromotionAdSummary> result = promotionService.getAdPromotions(lastId, size);

        List<PromotionAdInfo> content = result.getContent().stream()
                .map(ad -> new PromotionAdInfo(
                        ad.promotionId(), ad.category(), ad.title(),
                        ad.description(), ad.thumbnailUrl()))
                .toList();

        return new CursorResponse<>(content, result.getLastId(), result.isHasNext());
    }

    @GetMapping
    public CursorResponse<PromotionSummaryInfo> getPromotions(
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        CursorResponse<PromotionSummary> result = promotionService.getPromotions(lastId, size);

        List<PromotionSummaryInfo> content = result.getContent().stream()
                .map(p -> new PromotionSummaryInfo(
                        p.promotionId(), p.category(), p.title(), p.description(),
                        p.thumbnailUrl(), p.imageUrls(), p.discountType(),
                        p.discountValue(), p.point(), p.minPrice(), p.maxPrice(), p.endDate()))
                .toList();

        return new CursorResponse<>(content, result.getLastId(), result.isHasNext());
    }

    @PostMapping("/register")
    public PromotionRegisterInfo registerPromotion(
            @RequestBody PromotionRegisterRequest request,
            @RequestParam Long userId,
            @RequestParam(required = false) List<String> imageUrls) {
        Promotion promotion = promotionService.createPromotion(request, userId, null);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            promotion.attachImages(imageUrls);
        }

        return new PromotionRegisterInfo(
                promotion.getId(), promotion.getTitle(), promotion.getCode(),
                promotion.getPoint(), promotion.getDiscountType().name(),
                promotion.getDiscountValue(), promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getImages().stream().map(PromotionImage::getImageUrl).toList()
        );
    }

    @PatchMapping("/{promotionId}")
    public PromotionUpdateInfo updatePromotion(
            @PathVariable Long promotionId,
            @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        Long totalQuantity = Long.valueOf(body.get("totalQuantity").toString());

        PromotionUpdateRequest request = new PromotionUpdateRequest(title, description, totalQuantity);
        Promotion promotion = promotionService.updatePromotion(request, promotionId, userId);

        return new PromotionUpdateInfo(promotion.getTitle(), promotion.getDescription(), promotion.getTotalQuantity());
    }

    @GetMapping("/preset")
    public PromotionPresetInfo getPresetByCategory(@RequestParam String category) {
        Preset preset = presetService.getPresetByCategory(Category.valueOf(category));
        LocalDate now = LocalDate.now();
        return new PromotionPresetInfo(
                preset.getId(), preset.getCategory().name(), preset.getDescription(),
                preset.getPoint(), preset.getImageUrl(), preset.getDiscountType().name(),
                preset.getDiscountValue(), preset.getMinPrice(), preset.getMaxPrice(),
                preset.getValidPeriod(), now, now.plusDays(30)
        );
    }
}
