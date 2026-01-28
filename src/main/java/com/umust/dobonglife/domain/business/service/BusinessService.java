package com.umust.dobonglife.domain.business.service;


import com.umust.dobonglife.domain.business.domain.constant.BusinessCategory;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
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
import com.umust.dobonglife.domain.business.domain.constant.BusinessAmenity;


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

    @Transactional
    public void registerBusiness(BusinessRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        checkBusinessStatus(request.getBusinessNumber(), userId);

        Business business = Business.builder()
                .phoneNumber(request.getPhoneNumber())
                .managerName(request.getManagerName())
                .email(request.getEmail())
                .operatingHour(request.getOperatingHour())
                .user(user)
                .businessAmenity(request.getBusinessService().stream()
                        .map(BusinessAmenity::toEnum)
                        .toList())
                .businessCategory(BusinessCategory.toEnum(request.getBusinessCategory()))
                .businessNumber(request.getBusinessNumber())
                .build();

        businessRepository.save(business);
        //placeRepository.save(place);
    }

    @Transactional
    public void checkBusinessStatus(String businessNumber, Long userId) {

        Map<String, Object> response = webClientService.getCompanyStatus(businessNumber);

        String statusCode = parser.extractStatusCode(response);

        User me = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        me.setRole(Role.MANAGER);

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
}
