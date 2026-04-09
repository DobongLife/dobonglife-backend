package com.umust.dobonglife.domain.business.infrastructure.adapter;

import com.umust.dobonglife.domain.business.application.port.in.CheckBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.in.GetBusinessCategoryUseCase;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.port.BusinessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessPortAdapter implements BusinessPort {

    private final CheckBusinessUseCase checkBusinessUseCase;
    private final GetBusinessCategoryUseCase getBusinessCategoryUseCase;

    @Override
    public boolean checkBusiness(Long userId) {
        return checkBusinessUseCase.checkBusiness(userId);
    }

    @Override
    public Category getBusinessCategory(Long userId) {
        return getBusinessCategoryUseCase.getBusinessCategory(userId);
    }
}
