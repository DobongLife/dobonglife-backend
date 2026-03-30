package com.umust.dobonglife.domain.place.application.port.out;

public interface SavePlacePort {

    void addReview(Long placeId, Double rating);

    void removeReview(Long placeId, Double rating);
}
