package com.umust.dobonglife.domain.place.domain.repository.custom;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.domain.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepositoryCustom {
    List<Place> findByTheme(CourseTheme theme);
    List<Place> findDistinctPlacesByThemeLimit3(CourseTheme theme);
}
