package com.umust.dobonglife.domain.business.application.service;

import com.umust.dobonglife.domain.business.application.port.in.ManageBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.out.SaveBusinessPort;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCommandService implements ManageBusinessUseCase {

    private final SaveBusinessPort saveBusinessPort;

    @Override
    public Business save(Business business) {
        return saveBusinessPort.save(business);
    }

    @Override
    public void deleteById(Long id) {
        saveBusinessPort.deleteById(id);
    }

    @Override
    public void nullifyPlace(Long businessId) {
        saveBusinessPort.nullifyPlaceById(businessId);
    }
}
