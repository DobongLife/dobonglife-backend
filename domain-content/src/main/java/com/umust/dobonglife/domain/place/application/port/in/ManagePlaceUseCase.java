package com.umust.dobonglife.domain.place.application.port.in;

public interface ManagePlaceUseCase {

    void addReview(Long placeId, Double rating);

    void removeReview(Long placeId, Double rating);
}
