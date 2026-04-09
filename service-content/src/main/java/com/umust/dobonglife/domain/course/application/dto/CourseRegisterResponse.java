package com.umust.dobonglife.domain.course.application.dto;

public record CourseRegisterResponse(Long courseId) {

    public static CourseRegisterResponse from(Long courseId) {
        return new CourseRegisterResponse(courseId);
    }
}
