package com.umust.dobonglife.domain.business.application.port.in;

import com.umust.dobonglife.global.common.constant.Category;

public interface GetBusinessUseCase {

    boolean checkBusiness(Long userId);

    Category getBusinessCategory(Long userId);
}
