package com.umust.dobonglife.domain.business.infrastructure.adapter;

import com.umust.dobonglife.domain.business.application.BusinessService;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.port.BusinessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessPortAdapter implements BusinessPort {

    private final BusinessService businessService;

    @Override
    public boolean checkBusiness(Long userId) {
        return businessService.checkBusiness(userId);
    }

    @Override
    public Category getBusinessCategory(Long userId) {
        return businessService.getBusinessCategory(userId);
    }
}
