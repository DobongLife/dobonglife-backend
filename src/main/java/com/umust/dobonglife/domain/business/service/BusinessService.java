package com.umust.dobonglife.domain.business.service;


import com.umust.dobonglife.domain.business.controller.dto.response.BusinessPromotionResponse;
import com.umust.dobonglife.domain.business.controller.dto.response.BusinessResponse;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;

import com.umust.dobonglife.global.common.webclient.business.parser.BusinessStatusParser;
import com.umust.dobonglife.global.common.webclient.service.WebClientService;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final UserRepository userRepository;
    private final WebClientService webClientService;
    private final BusinessStatusParser parser;
    private final BusinessRepository businessRepository;
    private final PlaceRepository placeRepository;


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
    public BusinessResponse getBusinessResponse(Long businessId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_NOT_FOUND));
        return BusinessResponse.from(business);
    }

    @Transactional
    public BusinessResponse updateBusiness(Long userId, Long businessId, BusinessRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_NOT_FOUND));

        // 소유자 검증
        if (business.getUser() == null || !business.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_BUSINESS_OWNER);
        }

        // Business 필드 업데이트
        business.setBusinessNumber(request.getBusinessNumber());
        business.setEmail(request.getEmail());
        business.setManagerName(request.getManagerName());

        // Place 업데이트/교체
        Place updatedPlace = placeService.resolvePlaceForUpdate(business, request);
        business.setPlace(updatedPlace);

        return BusinessResponse.from(business);
    }

    @Transactional(readOnly = true)
    public BusinessPromotionResponse getBusinessPromotion(Long userId, Long businessId){


    }



    @Transactional(readOnly = true)
    public Business getBusinessByUser(Long userId) {
        return businessRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    public String getCategoryUserId(Long userId) {
        Business businessByUser = getBusinessByUser(userId);
        return businessByUser.getPlace().getCategory().getDescription();
    }
}
