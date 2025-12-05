package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.entity.Course;

import java.util.List;

public record CourseSummaryResponse(
        Integer id,
        List<String> imageUrls,
        String title,
        String subTitle,
        List<String> tags,
        CourseLevel level
) {
    public static CourseSummaryResponse from(Course course) {
        return new CourseSummaryResponse(
                course.getId(),
                course.getImageUrls(),
                course.getTitle(),
                course.getSubTitle(),
                course.getTags(),
                course.getLevel()
        );
    }
}

