package com.umust.dobonglife.domain.course.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseResponse {

    private Long courseId;
    private String title;
    private String subtitle;
    private String imageUrl;
    private List<String> tags;
    private CourseLevel level;
    private Long reviewCount;
    private Double averageRating;

    @QueryProjection
    public CourseResponse(
            Long courseId,
            String title,
            String subtitle,
            String imageUrl,
            List<String> tags,
            CourseLevel level,
            Long reviewCount,
            Double averageRating
    ) {
        this.courseId = courseId;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.level = level;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }
}
