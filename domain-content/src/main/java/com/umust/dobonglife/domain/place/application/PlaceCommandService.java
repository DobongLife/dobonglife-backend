package com.umust.dobonglife.domain.place.application;

import com.umust.dobonglife.domain.place.application.port.in.ManagePlaceUseCase;
import com.umust.dobonglife.domain.place.application.port.out.SavePlacePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceCommandService implements ManagePlaceUseCase {

    private final SavePlacePort savePlacePort;

    @Override
    public void addReview(Long placeId, Double rating) {
        savePlacePort.addReview(placeId, rating);
    }

    @Override
    public void removeReview(Long placeId, Double rating) {
        savePlacePort.removeReview(placeId, rating);
    }
}
