package com.umust.dobonglife.domain.business.application;

import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import com.umust.dobonglife.domain.business.exception.BusinessDomainException;
import com.umust.dobonglife.domain.business.exception.BusinessErrorCode;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessService {
    private final BusinessRepository businessRepository;

    public boolean checkBusiness(Long userId) {
        return businessRepository.existsByUserId(userId);
    }

    public Category getBusinessCategory(Long userId) {
        String categoryName = businessRepository.findCategoryByUserId(userId)
                .orElseThrow(() -> new BusinessDomainException(BusinessErrorCode.BUSINESS_CATEGORY_NOT_FOUND));
        return Category.valueOf(categoryName);
    }
}
