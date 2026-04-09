package com.umust.dobonglife.domain.course.application.dto;

import com.umust.dobonglife.domain.course.domain.entity.*;
import com.umust.dobonglife.domain.course.domain.vo.Level;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.stream.IntStream;

public record CreateCourseRequest(
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

        List<String> imageUrls,

        @NotNull @Size(min = 1)
        List<@NotNull Theme> themes,

        @Valid @NotNull @Size(min = 1)
        List<CoursePlanRequest> plans,

        List<@NotBlank @Size(max = 20) String> tags
) {

    public Course toEntity(Long userId) {
        return Course.create(
                userId,
                title,
                subTitle,
                level,
                duration,
                content,
                imageUrls != null ? imageUrls : List.of(),
                themes.stream().map(CourseTheme::of).toList(),
                toCoursePlans(),
                tags != null ? tags.stream().map(CourseTag::of).toList() : List.of()
        );
    }

    private List<CoursePlan> toCoursePlans() {
        return IntStream.range(0, plans.size())
                .mapToObj(i -> CoursePlan.of(
                        plans.get(i).placeId(),
                        (short) i,
                        plans.get(i).title(),
                        plans.get(i).content()
                ))
                .toList();
    }

    public record CoursePlanRequest(
            Long placeId,
            @NotBlank @Size(max = 50) String title,
            @Size(max = 100) String content
    ) {}
}
