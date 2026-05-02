package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.port.dto.content.PlaceSummaryInfo;

import java.util.List;
import java.util.Map;

public interface PlacePort {
    List<PlaceSummaryInfo> getAllActivePlaces(Long userId);
    Map<String, Object> getPlaceDetail(Long placeId, Long userId, Long lastId, int size);
}
