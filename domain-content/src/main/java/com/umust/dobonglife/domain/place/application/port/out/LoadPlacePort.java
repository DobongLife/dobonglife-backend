package com.umust.dobonglife.domain.place.application.port.out;

import com.umust.dobonglife.domain.place.domain.entity.Place;

import java.util.List;
import java.util.Optional;

public interface LoadPlacePort {

    Optional<Place> findByIdAndActive(Long placeId);

    List<Place> findAllByIds(List<Long> placeIds);

    List<Place> findAllActive();
}
