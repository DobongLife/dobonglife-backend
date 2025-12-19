package com.umust.dobonglife.domain.course.controller.dto.response;

public record CourseDeleteResponse(
        Long courseId
) {
    public static CourseDeleteResponse from(Long courseId) {
        return new CourseDeleteResponse(courseId);
    }
}
