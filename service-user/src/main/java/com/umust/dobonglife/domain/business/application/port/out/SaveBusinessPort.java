package com.umust.dobonglife.domain.business.application.port.out;

import com.umust.dobonglife.domain.business.domain.entity.Business;

public interface SaveBusinessPort {

    Business save(Business business);

    void deleteById(Long id);

    void nullifyPlaceById(Long businessId);
}
