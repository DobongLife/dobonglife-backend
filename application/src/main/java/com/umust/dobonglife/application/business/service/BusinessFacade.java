package com.umust.dobonglife.application.business.service;

import com.umust.dobonglife.domain.business.application.port.in.GetBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.in.ManageBusinessUseCase;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.exception.BusinessErrorCode;
import com.umust.dobonglife.domain.business.exception.BusinessException;
import com.umust.dobonglife.domain.coupon.application.CouponService;
import com.umust.dobonglife.domain.place.application.PlaceService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceDetail;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.common.image.ImageUploader;
import com.umust.dobonglife.infra.webclient.business.parser.BusinessStatusParser;
import com.umust.dobonglife.infra.webclient.service.WebClientService;
import org.springframework.data.domain.Slice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessFacade {

    private final GetBusinessUseCase getBusinessUseCase;
    private final ManageBusinessUseCase manageBusinessUseCase;
    private final PlaceService placeService;
    private final PromotionService promotionService;
    private final CouponService couponService;
    private final WebClientService webClientService;
    private final BusinessStatusParser businessStatusParser;
    private final ImageUploader imageUploader;

    @Transactional(readOnly = true)
    public BusinessInfo getBusinessInfo(Long userId) {
        Business business = getBusinessUseCase.getBusinessByUser(userId);
        Place place = placeService.getPlace(business.getPlaceId());
        return new BusinessInfo(business, place);
    }

    @Transactional
    public void registerBusiness(RegisterCommand command, Long userId, List<MultipartFile> imageFiles) {
        checkBusinessStatus(command.businessNumber());

        Place place;
        if (command.placeId() != null) {
            place = placeService.getPlace(command.placeId());
        } else {
            place = createPlace(command, imageFiles);
        }

        Business business = Business.builder()
                .userId(userId)
                .placeId(place.getId())
                .category(Category.valueOf(command.category()))
                .businessNumber(command.businessNumber())
                .managerName(command.managerName())
                .email(command.email())
                .build();

        manageBusinessUseCase.save(business);
    }

    @Transactional
    public BusinessInfo updateBusiness(Long userId, UpdateCommand command, List<MultipartFile> imageFiles) {
        Business business = getBusinessUseCase.getBusinessByUser(userId);
        if (!business.getUserId().equals(userId)) {
            throw new BusinessException(BusinessErrorCode.NOT_BUSINESS_OWNER);
        }

        business.updateInfo(command.email(), command.managerName());
        manageBusinessUseCase.save(business);

        Place place = placeService.getPlace(business.getPlaceId());
        return new BusinessInfo(business, place);
    }

    @Transactional(readOnly = true)
    public PromotionPage getBusinessPromotions(Long userId, Long lastId, int size) {
        Business business = getBusinessUseCase.getBusinessByUser(userId);
        Slice<Promotion> slice = promotionService.getPromotionSliceByBusinessId(business.getId(), lastId, size);

        List<Long> promotionIds = slice.getContent().stream()
                .map(Promotion::getId)
                .toList();

        Map<Long, Long> usedCountMap = promotionIds.isEmpty()
                ? Map.of()
                : couponService.getUsedCountByPromotionIds(promotionIds);

        return new PromotionPage(slice.getContent(), usedCountMap, slice.hasNext());
    }

    private void checkBusinessStatus(String businessNumber) {
        Map<String, Object> response = webClientService.getCompanyStatus(businessNumber);
        String statusCode = businessStatusParser.extractStatusCode(response);
        if (!"01".equals(statusCode)) {
            throw new BusinessException(BusinessErrorCode.INVALID_BUSINESS_NUMBER);
        }
    }

    private Place createPlace(RegisterCommand command, List<MultipartFile> imageFiles) {
        Place place = Place.builder()
                .category(Category.valueOf(command.category()))
                .name(command.businessName())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .build();

        PlaceDetail detail = PlaceDetail.builder()
                .subName(command.subName())
                .address(command.businessAddress())
                .operatingHour(command.operatingHour())
                .contact(command.contact())
                .content(command.content())
                .build();
        place.attachDetail(detail);

        if (command.themes() != null) {
            List<Theme> themes = command.themes().stream()
                    .map(Theme::valueOf)
                    .toList();
            place.attachThemes(themes);
        }

        List<String> imageUrls = imageUploader.uploadImages(imageFiles);
        if (!imageUrls.isEmpty()) {
            place.attachImages(imageUrls);
        }

        return placeService.savePlace(place);
    }

    public record BusinessInfo(Business business, Place place) {}

    public record RegisterCommand(
            Long placeId, String subName, String businessName, String content,
            String contact, String email, String businessAddress, String operatingHour,
            String managerName, String businessNumber, Double latitude, Double longitude,
            String category, List<String> themes
    ) {}

    public record UpdateCommand(
            String subName, String businessName, String content, String contact,
            String email, String operatingHour, String managerName, String category
    ) {}

    public record PromotionPage(
            List<Promotion> promotions,
            Map<Long, Long> usedCountMap,
            boolean hasNext
    ) {}
}
