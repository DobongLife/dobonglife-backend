package com.umust.dobonglife.domain.place.domain.repository.custom;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepositoryCustom {
    List<PlaceSummaryResponse> findPlaceSummariesByTheme(CourseTheme theme, Integer size);
    List<PlaceSummaryResponse> findPlaceSummaries();
}
