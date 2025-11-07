package com.umust.dobonglife.course.domain.repository.custom;

import com.umust.dobonglife.course.domain.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepositoryCustom {
    Page<Course> findAllRandomOrder(Pageable pageable);
}
