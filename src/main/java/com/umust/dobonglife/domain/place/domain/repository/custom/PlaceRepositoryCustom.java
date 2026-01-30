package com.umust.dobonglife.domain.place.domain.repository.custom;

import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PlaceRepositoryCustom {
    List<PlaceSummaryResponse> findPlaceSummaries(Long userId);
    Slice<Place> findLikedPlaceSummaries(Long userId, Long lastId, Pageable pageable);
}
