package com.umust.dobonglife.domain.course.repository;

import com.umust.dobonglife.domain.course.model.CoursePlace;
import com.umust.dobonglife.domain.course.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePlaceRepository extends JpaRepository<CoursePlace, Long> {
    List<CoursePlace> findByTheme(Theme theme);
}
