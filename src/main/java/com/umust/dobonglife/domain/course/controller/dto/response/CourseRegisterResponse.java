package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.domain.entity.Course;

import java.time.LocalDateTime;

public record CourseRegisterResponse(
        Long courseId,
        String title,
        LocalDateTime createdAt
) {
    public static CourseRegisterResponse from(Course course) {
        return new CourseRegisterResponse(
                course.getId(),
                course.getBasicInfo().getTitle(),
                LocalDateTime.now()
        );
    }
}
