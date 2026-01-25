package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.domain.entity.Course;

import java.time.LocalDateTime;

import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.COURSE_CREATE;

public record CourseRegisterResponse(
        Long courseId,
        String title,
        LocalDateTime createdAt,
        Long point
) {
    public static CourseRegisterResponse from(Course course) {
        return new CourseRegisterResponse(
                course.getId(),
                course.getBasicInfo().getTitle(),
                LocalDateTime.now(),
                COURSE_CREATE.getPoint()
        );
    }
}
