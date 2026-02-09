package com.umust.dobonglife.domain.course.controller.dto.response;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.global.common.Identifiable;

import java.util.List;

public record CourseSummaryResponse(
        Long id,
        List<String> imageUrls,
        String title,
        String subTitle,
        List<String> tags,
        CourseLevel level,
        boolean liked

)implements Identifiable{
    public static CourseSummaryResponse of(Course course, boolean liked) {
        return new CourseSummaryResponse(
                course.getId(),
                course.getImageUrls(),
                course.getTitle(),
                course.getSubTitle(),
                course.getTags(),
                course.getLevel(),
                liked
        );
    }

    @Override
    public Long getId() {
        return id;
    }
}

