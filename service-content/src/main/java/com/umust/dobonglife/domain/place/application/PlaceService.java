package com.umust.dobonglife.domain.place.application;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.place.exception.PlaceErrorCode;
import com.umust.dobonglife.domain.place.exception.PlaceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umust.dobonglife.global.common.model.BaseStatus;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {
    private final PlaceRepository placeRepository;

    public Place getPlace(Long placeId) {
        return placeRepository.findByIdAndStatus(placeId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new PlaceException(PlaceErrorCode.PLACE_NOT_FOUND));
    }

    public Map<Long, Place> getPlacesInBatch(List<Long> placeIds) {
        return placeRepository.findAllByIdIn(placeIds).stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));
    }

    public List<Place> getAllActivePlaces() {
        return placeRepository.findAllByStatus(BaseStatus.ACTIVE);
    }

    @Transactional
    public Place savePlace(Place place) {
        return placeRepository.save(place);
    }
}