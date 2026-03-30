package com.umust.dobonglife.domain.place.application;

import com.umust.dobonglife.domain.place.application.port.in.GetPlaceUseCase;
import com.umust.dobonglife.domain.place.application.port.in.ManagePlaceUseCase;
import com.umust.dobonglife.domain.place.application.port.out.LoadPlacePort;
import com.umust.dobonglife.domain.place.application.port.out.SavePlacePort;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.exception.PlaceErrorCode;
import com.umust.dobonglife.domain.place.exception.PlaceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService implements GetPlaceUseCase, ManagePlaceUseCase {

    private final LoadPlacePort loadPlacePort;
    private final SavePlacePort savePlacePort;

    @Override
    public Place getPlace(Long placeId) {
        return loadPlacePort.findByIdAndActive(placeId)
                .orElseThrow(() -> new PlaceException(PlaceErrorCode.PLACE_NOT_FOUND));
    }

    @Override
    public Map<Long, Place> getPlacesInBatch(List<Long> placeIds) {
        return loadPlacePort.findAllByIds(placeIds).stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));
    }

    @Override
    public List<Place> getAllActivePlaces() {
        return loadPlacePort.findAllActive();
    }

    @Override
    @Transactional
    public void addReview(Long placeId, Double rating) {
        savePlacePort.addReview(placeId, rating);
    }

    @Override
    @Transactional
    public void removeReview(Long placeId, Double rating) {
        savePlacePort.removeReview(placeId, rating);
    }
}
