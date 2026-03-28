package com.umust.dobonglife.domain.course.application.dto;

import com.umust.dobonglife.domain.course.domain.entity.*;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;

public record CourseDetailResponse(
        Long courseId,
        String title,
        String subTitle,
        String level,
        Long duration,
        String content,
        List<String> imageUrls,
        List<String> themes,
        List<String> tags,
        List<CoursePlanDetail> plans,
        Double averageRating,
        Long reviewCount,
        boolean isLiked,
        CursorResponse<ReviewSummaryResponse> reviews
) {

    public static CourseDetailResponse of(Course course, boolean isLiked, CursorResponse<ReviewSummaryResponse> reviews) {
        return new CourseDetailResponse(
                course.getId(),
                course.getTitle(),
                course.getSubTitle(),
                course.getLevel() != null ? course.getLevel().name() : null,
                course.getDuration(),
                course.getContent(),
                course.getImages().stream().map(CourseImage::getImageUrl).toList(),
                course.getThemes().stream().map(t -> t.getTheme().name()).toList(),
                course.getTags().stream().map(CourseTag::getTag).toList(),
                course.getPlans().stream().map(CoursePlanDetail::from).toList(),
                course.getAverageRating(),
                course.getReviewCount(),
                isLiked,
                reviews
        );
    }

    public record CoursePlanDetail(
            Long id,
            Long placeId,
            Short sortOrder,
            String title,
            String content
    ) {
        public static CoursePlanDetail from(CoursePlan plan) {
            return new CoursePlanDetail(
                    plan.getId(),
                    plan.getPlaceId(),
                    plan.getSortOrder(),
                    plan.getTitle(),
                    plan.getContent()
            );
        }
    }
}
