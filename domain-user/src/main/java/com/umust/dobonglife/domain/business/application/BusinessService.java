package com.umust.dobonglife.domain.business.application;

import com.umust.dobonglife.domain.business.application.port.in.GetBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.out.LoadBusinessPort;
import com.umust.dobonglife.domain.business.exception.BusinessDomainException;
import com.umust.dobonglife.domain.business.exception.BusinessErrorCode;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessService implements GetBusinessUseCase {
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
}
