package com.umust.dobonglife.domain.course.application.port.in;

import com.umust.dobonglife.domain.course.application.dto.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.application.dto.CreateCourseRequest;
import com.umust.dobonglife.domain.course.application.dto.UpdateCourseRequest;

public interface ManageCourseUseCase {

    CourseRegisterResponse createCourse(Long userId, CreateCourseRequest request);

    CourseRegisterResponse updateCourse(Long userId, Long courseId, UpdateCourseRequest request);

    void deleteCourse(Long userId, Long courseId);

    void addReview(Long courseId, Double rating);

    void removeReview(Long courseId, Double rating);
}
