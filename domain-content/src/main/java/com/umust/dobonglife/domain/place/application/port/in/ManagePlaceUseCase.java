package com.umust.dobonglife.domain.place.application.port.in;

import com.umust.dobonglife.domain.place.domain.entity.Place;

public interface ManagePlaceUseCase {

    void addReview(Long placeId, Double rating);

    void removeReview(Long placeId, Double rating);

    Place savePlace(Place place);
}
