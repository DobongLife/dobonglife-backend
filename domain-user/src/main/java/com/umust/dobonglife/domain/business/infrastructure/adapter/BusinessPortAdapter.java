package com.umust.dobonglife.domain.business.infrastructure.adapter;

import com.umust.dobonglife.domain.business.application.port.in.GetBusinessUseCase;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.port.BusinessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessPortAdapter implements BusinessPort {

    private final GetBusinessUseCase getBusinessUseCase;

    @Override
    public boolean checkBusiness(Long userId) {
        return getBusinessUseCase.checkBusiness(userId);
    }

    @Override
    public Category getBusinessCategory(Long userId) {
        return getBusinessUseCase.getBusinessCategory(userId);
    }
}
