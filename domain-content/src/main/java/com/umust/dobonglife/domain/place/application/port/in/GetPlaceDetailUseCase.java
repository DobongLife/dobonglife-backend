package com.umust.dobonglife.domain.place.application.port.in;

import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;

public interface GetPlaceDetailUseCase {

    PlaceDetailResponse getPlaceDetail(Long placeId, Long userId, Long lastId, int size);
}
