package com.umust.dobonglife.domain.place.infrastructure.adapter;

import com.umust.dobonglife.domain.place.application.port.out.LoadPlacePort;
import com.umust.dobonglife.domain.place.application.port.out.SavePlacePort;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.infrastructure.jpa.PlaceJpaRepository;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlacePersistenceAdapter implements LoadPlacePort, SavePlacePort {

    private final PlaceJpaRepository placeJpaRepository;

    // ── LoadPlacePort ──

    @Override
    public Optional<Place> findByIdAndActive(Long placeId) {
        return placeJpaRepository.findByIdAndStatus(placeId, BaseStatus.ACTIVE);
    }

    @Override
    public List<Place> findAllByIds(List<Long> placeIds) {
        return placeJpaRepository.findAllByIdIn(placeIds);
    }

    @Override
    public List<Place> findAllActive() {
        return placeJpaRepository.findAllByStatus(BaseStatus.ACTIVE);
    }

    // ── SavePlacePort ──

    @Override
    public void addReview(Long placeId, Double rating) {
        placeJpaRepository.addReview(placeId, rating);
    }

    @Override
    public void removeReview(Long placeId, Double rating) {
        placeJpaRepository.removeReview(placeId, rating);
    }
}
