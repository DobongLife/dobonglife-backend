package com.umust.dobonglife.domain.place.application.port.out;

import com.umust.dobonglife.domain.place.domain.entity.Place;

public interface SavePlacePort {

    void addReview(Long placeId, Double rating);

    void removeReview(Long placeId, Double rating);

    Place save(Place place);
}
