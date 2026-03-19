package com.umust.dobonglife.global.port.content;

import com.umust.dobonglife.global.port.dto.content.PlaceSummaryInfo;

import java.util.List;

public interface PlacePort {
    List<PlaceSummaryInfo> getAllActivePlaces(Long userId);
}
