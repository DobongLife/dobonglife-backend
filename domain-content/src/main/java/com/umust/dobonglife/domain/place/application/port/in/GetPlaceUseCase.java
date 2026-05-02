package com.umust.dobonglife.domain.place.application.port.in;

import com.umust.dobonglife.domain.place.domain.entity.Place;

import java.util.List;
import java.util.Map;

public interface GetPlaceUseCase {

    Place getPlace(Long placeId);

    Map<Long, Place> getPlacesInBatch(List<Long> placeIds);

    List<Place> getAllActivePlaces();
}
