package com.umust.dobonglife.domain.course.application.port.out;

import com.umust.dobonglife.domain.course.domain.entity.Course;

public interface SaveCoursePort {

    Course save(Course course);

    void addReview(Long courseId, Double rating);

    void removeReview(Long courseId, Double rating);
}
