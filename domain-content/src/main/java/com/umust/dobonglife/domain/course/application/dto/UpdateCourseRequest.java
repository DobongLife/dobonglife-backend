package com.umust.dobonglife.domain.course.application.dto;

import com.umust.dobonglife.domain.course.domain.entity.CoursePlan;
import com.umust.dobonglife.domain.course.domain.entity.CourseTag;
import com.umust.dobonglife.domain.course.domain.entity.CourseTheme;
import com.umust.dobonglife.domain.course.domain.vo.Level;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.stream.IntStream;

public record UpdateCourseRequest(
        @NotBlank @Size(max = 50)
        String title,

        @Size(max = 100)
        String subTitle,

        @NotNull
        Level level,

        @NotNull @Positive
        Long duration,

        @Size(max = 500)
        String content,

        List<String> newImageUrls,

        List<String> deleteImageUrls,

        @NotNull @Size(min = 1)
        List<Theme> themes,

        @Valid @NotNull @Size(min = 1)
        List<CreateCourseRequest.CoursePlanRequest> plans,

        List<@Size(max = 20) String> tags
) {

    public List<CourseTheme> toCourseThemes() {
        return themes.stream().map(CourseTheme::of).toList();
    }

    public List<CoursePlan> toCoursePlans() {
        return IntStream.range(0, plans.size())
                .mapToObj(i -> CoursePlan.of(
                        plans.get(i).placeId(),
                        (short) i,
                        plans.get(i).title(),
                        plans.get(i).content()
                ))
                .toList();
    }

    public List<CourseTag> toCourseTags() {
        return tags != null ? tags.stream().map(CourseTag::of).toList() : List.of();
    }
}
