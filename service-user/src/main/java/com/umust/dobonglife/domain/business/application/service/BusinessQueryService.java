package com.umust.dobonglife.domain.business.application.service;

import com.umust.dobonglife.domain.business.application.port.in.CheckBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.in.GetBusinessCategoryUseCase;
import com.umust.dobonglife.domain.business.application.port.in.GetBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.out.LoadBusinessPort;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.exception.BusinessDomainException;
import com.umust.dobonglife.domain.business.exception.BusinessErrorCode;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessQueryService implements CheckBusinessUseCase, GetBusinessCategoryUseCase, GetBusinessUseCase {

    private final LoadBusinessPort loadBusinessPort;

    @Override
    public boolean checkBusiness(Long userId) {
        return loadBusinessPort.existsByUserId(userId);
    }

    @Override
    public Category getBusinessCategory(Long userId) {
        return loadBusinessPort.findCategoryByUserId(userId)
                .orElseThrow(() -> new BusinessDomainException(BusinessErrorCode.BUSINESS_CATEGORY_NOT_FOUND));
    }

    @Override
    public Business getBusinessByUser(Long userId) {
        return loadBusinessPort.findByUserId(userId)
                .orElseThrow(() -> new BusinessDomainException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }

    @Override
    public Long getBusinessPlaceId(Long userId) {
        Business business = getBusinessByUser(userId);
        if (business.getPlaceId() == null) {
            throw new BusinessDomainException(BusinessErrorCode.BUSINESS_NOT_FOUND);
        }
        return business.getPlaceId();
    }
}
