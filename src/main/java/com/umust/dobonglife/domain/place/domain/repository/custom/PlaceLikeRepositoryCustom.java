package com.umust.dobonglife.domain.place.domain.repository.custom;

import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;

import java.util.Optional;

public interface PlaceLikeRepositoryCustom {
    Optional<PlaceLike> findByUserIdAndPlaceId(Long userId, Long placeId);
}
