package com.umust.dobonglife.application.business.service;

import com.umust.dobonglife.domain.business.application.port.in.GetBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.in.ManageBusinessUseCase;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.place.application.PlaceService;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessFacade {

    private final GetBusinessUseCase getBusinessUseCase;
    private final ManageBusinessUseCase manageBusinessUseCase;
    private final PlaceService placeService;

    @Transactional(readOnly = true)
    public BusinessInfo getBusinessInfo(Long userId) {
        Business business = getBusinessUseCase.getBusinessByUser(userId);
        Place place = placeService.getPlace(business.getPlaceId());
        return new BusinessInfo(business, place);
    }

    public record BusinessInfo(Business business, Place place) {}
}
