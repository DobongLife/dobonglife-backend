package com.umust.dobonglife.domain.course.repository;

import com.umust.dobonglife.domain.course.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
