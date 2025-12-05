package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailResponse(
        Integer id,
        String title,
        String subTitle,
        Double reviewRating,
        int reviewNum,
        Double duration,
        CourseLevel level,
        List<String> tags,
        List<String> imageUrls,
        String content,
        String meetingPlace,
        String contact,
        String cost,
        Integer maxNum, // 이동거리 대신
        String ageLimit,
        String cancelPolicy,
        String weatherPolicy,
        List<String> highlights,
        List<String> exclusions,
        List<String> inclusions,
        List<CoursePlanDto> plans
        // TODO: 리뷰 평점, 리뷰 개수
) {
    public static CourseDetailResponse from(Course course, List<CoursePlans> plans) {
        return new CourseDetailResponse(
                course.getId(),
                course.getTitle(),
                course.getSubTitle(),
                course.getReviewRating(),
                course.getReviewNum(),
                course.getDuration(),
                course.getLevel(),
                course.getTags(),
                course.getImageUrls(),
                course.getContent(),
                course.getMeetingPlace(),
                course.getContact(),
                course.getCost(),
                course.getMaxNum(),
                course.getAgeLimit(),
                course.getCancelPolicy(),
                course.getWeatherPolicy(),
                course.getHighlights(),
                course.getExclusions(),
                course.getInclusions(),
                plans.stream()
                        .map(CoursePlanDto::from)
                        .toList()
        );
    }

    public record CoursePlanDto(
            Integer id,
            LocalDateTime dateTime,
            String title,
            String content
    ) {
        public static CoursePlanDto from(CoursePlans plan) {
            return new CoursePlanDto(
                    plan.getId(),
                    plan.getDateTime(),
                    plan.getTitle(),
                    plan.getContent()
            );
        }
    }
}

