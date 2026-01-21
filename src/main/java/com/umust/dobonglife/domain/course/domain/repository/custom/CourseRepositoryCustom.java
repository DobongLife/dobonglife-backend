package com.umust.dobonglife.domain.course.domain.repository.custom;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CourseRepositoryCustom {
    List<Course> findByTheme(CourseTheme theme);
    List<Course> findDistinctPlacesByThemeLimit3(CourseTheme theme);
    Slice<Course> findLikedCourses(Long userId, Long lastId, Pageable pageable);
}
