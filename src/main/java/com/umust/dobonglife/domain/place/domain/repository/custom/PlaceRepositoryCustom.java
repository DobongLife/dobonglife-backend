package com.umust.dobonglife.domain.place.domain.repository.custom;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.domain.entity.Place;

import java.util.List;

public interface PlaceRepositoryCustom {
    List<Place> findDistinctPlacesByThemeLimit3(CourseTheme theme);
}
