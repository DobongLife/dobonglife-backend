package com.umust.dobonglife.domain.business.application.port.in;

import com.umust.dobonglife.domain.business.domain.entity.Business;

public interface GetBusinessUseCase {

    Business getBusinessByUser(Long userId);

    Long getBusinessPlaceId(Long userId);
}
