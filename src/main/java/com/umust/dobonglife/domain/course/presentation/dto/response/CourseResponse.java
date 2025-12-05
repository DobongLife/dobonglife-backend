package com.umust.dobonglife.domain.course.presentation.dto.response;

import com.umust.dobonglife.domain.course.domain.entity.Course;

import java.util.List;

public record CourseResponse(
        Long id,
        String title,
        String subTitle,
        Double duration,
        List<String> tags,
        List<String> imageUrls,
        String meetingPlace,
        String cost,
        Integer maxNum
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getSubTitle(),
                course.getDuration(),
                course.getTags(),
                course.getImageUrls(),
                course.getMeetingPlace(),
                course.getCost(),
                course.getMaxNum()
        );
    }
}

