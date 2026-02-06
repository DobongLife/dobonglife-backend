package com.umust.dobonglife.domain.business.service;


import com.umust.dobonglife.domain.business.controller.dto.request.BusinessUpdateRequest;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessPromotionResponse;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessResponse;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import com.umust.dobonglife.domain.business.service.dto.CouponUsageCount;
import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.common.webclient.business.parser.BusinessStatusParser;
import com.umust.dobonglife.global.common.webclient.service.WebClientService;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final UserRepository userRepository;
    private final WebClientService webClientService;
    private final BusinessStatusParser parser;
    private final BusinessRepository businessRepository;
    private final PlaceRepository placeRepository;
    private final CouponRepository couponRepository;
    private final PromotionRepository promotionRepository;

    private static final String VALID_CODE = "01";
    private final PlaceService placeService;

    @Transactional
    public void registerBusiness(BusinessRequest request, Long userId, List<MultipartFile> imageFiles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkBusinessStatus(request.getBusinessNumber());

        Long placeId = request.getPlaceId();
        if(placeId == null) {
            placeId = placeService.createPlaceForBusiness(request, imageFiles);
        }

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        Business business = Business.builder()
                .businessNumber(request.getBusinessNumber())
                .email(request.getEmail())
                .managerName(request.getManagerName())
                .place(place)
                .user(user)
                .build();
        user.setRole(Role.MANAGER);
        businessRepository.save(business);
    }

    @Transactional
    public void checkBusinessStatus(String businessNumber) {

        Map<String, Object> response = webClientService.getCompanyStatus(businessNumber);

        String statusCode = parser.extractStatusCode(response);

        if (!VALID_CODE.equals(statusCode)) {
            throw new IllegalStateException("유효하지 않은 사업자 번호입니다.");
        }
    }

    @Transactional(readOnly = true)
    public Long returnBusinessPlaceId(Long userId) {

        Business business = businessRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_NOT_FOUND));

        if (business.getPlace() == null) {
            throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
        }

        return business.getPlace().getId();
    }

    @Transactional(readOnly = true)
    public BusinessResponse getBusinessResponse(Long userId) {
        Business business = getBusinessByUser(userId);
        return BusinessResponse.from(business);
    }

    @Transactional
    public BusinessResponse updateBusiness(Long userId, BusinessUpdateRequest request) {
        Business business = getBusinessByUser(userId);
        // 소유자 검증
        if (business.getUser() == null || !business.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_BUSINESS_OWNER);
        }

        // Business 필드 업데이트
        business.setEmail(request.getEmail());
        business.setManagerName(request.getManagerName());

        // Place 업데이트/교체
        Place updatedPlace = placeService.resolvePlaceForUpdate(business, request);
        business.setPlace(updatedPlace);

        return BusinessResponse.from(business);
    }

    @Transactional(readOnly = true)
    public CursorResponse<BusinessPromotionResponse> getBusinessPromotion(
            Long userId, Long lastId, int size
    ) {
        // 1) Promotion 조회 비어있으면, 조기 리턴
        Pageable pageable = PageRequest.of(0, size);
        Slice<Promotion> promotions =
                promotionRepository.findPromotionNoOffsetByUserId(userId, lastId, pageable);

        if (promotions.isEmpty()) {
            return new CursorResponse<>(List.of(), false);
        }

        // 2) promotionIds 추출
        List<Long> promotionIds = promotions.getContent().stream()
                .map(Promotion::getId)
                .toList();

        // 3) 쿠폰 집계
        List<CouponUsageCount> rows =
                couponRepository.countCouponUsageByPromotionIds(promotionIds);

        Map<Long, CouponUsageCount> usageMap = rows.stream()
                .collect(Collectors.toMap(
                        CouponUsageCount::promotionId,
                        Function.identity()
                ));

        // 4) Promotion → BusinessPromotionResponse 변환
        LocalDate today = LocalDate.now();

        List<BusinessPromotionResponse> content = promotions.getContent().stream()
                .map(p -> {
                    CouponUsageCount usage = usageMap.get(p.getId());
                    long total = p.getTotalQuantity();
                    long used = (usage == null) ? 0 : usage.usedCount();
                    int usedValue = (total == 0)
                            ? 0
                            : (int) Math.round((double) used * 100 / total);

                    boolean inPeriod =
                            !today.isBefore(p.getStartDate()) &&
                                    !today.isAfter(p.getEndDate());

                    return BusinessPromotionResponse.of(
                            p.getId(),
                            p.getTitle(),
                            p.getStartDate(),
                            p.getEndDate(),
                            inPeriod,
                            p.getDiscountType(),
                            p.getDiscountValue(),
                            usedValue,
                            total,
                            used,
                            p.getCode(),
                            p.getDescription(),
                            p.getValidPeriod()
                    );
                })
                .toList();

        // 5) CursorResponse로 감싸서 반환
        return new CursorResponse<>(content, promotions.hasNext());
    }

    @Transactional(readOnly = true)
    public Business getBusinessByUser(Long userId) {
        return businessRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    public Category getCategoryUserId(Long userId) {
        Business businessByUser = getBusinessByUser(userId);
        return businessByUser.getPlace().getCategory();
    }
}
