package com.umust.dobonglife.domain.business.application.port.in;

import com.umust.dobonglife.domain.business.domain.entity.Business;

public interface ManageBusinessUseCase {

    Business save(Business business);

    void deleteById(Long id);

    void nullifyPlace(Long businessId);
}
