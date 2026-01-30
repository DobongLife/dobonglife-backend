package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.ReviewSummary;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CourseDescription;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 코스 상세 조회 응답
 * 모든 정보 포함
 * description은 fetch join으로 가져와야 함
 */
public record CourseDetailResponse(
        Long id,
        UserInfo userInfo,
        BasicInfo basicInfo,
        DescriptionInfo descriptionInfo,
        ReviewSummary reviewSummary,
        List<String> imageUrls,
        List<CoursePlanDto> plans,
        CursorResponse<ReviewSummaryResponse> reviews
) {
    public static CourseDetailResponse from(Course course, List<CoursePlans> plans, CursorResponse<ReviewSummaryResponse> reviews, boolean isRemoved, boolean isFavorite) {
        return new CourseDetailResponse(
                course.getId(),
                UserInfo.from(isRemoved, isFavorite),
                BasicInfo.from(course),
                DescriptionInfo.from(course.getDescription()),
                ReviewSummary.from(course),
                course.getImageUrls(),
                plans.stream().map(CoursePlanDto::from).toList(),
                reviews
        );
    }

    /**
     * 사용자 정보
     */
    public record UserInfo(
            boolean isRemoved,
            boolean isFavorite
    ) {
        public static UserInfo from(boolean isRemoved, boolean isFavorite) {
            return new UserInfo(
                    isRemoved,
                    isFavorite
            );
        }
    }

    /**
     * 기본 정보
     */
    public record BasicInfo(
            String title,
            String subTitle,
            Long duration,
            CourseLevel level,
            List<CourseTheme> themes,
            List<String> tags
    ) {
        public static BasicInfo from(Course course) {
            return new BasicInfo(
                    course.getBasicInfo().getTitle(),
                    course.getBasicInfo().getSubTitle(),
                    course.getBasicInfo().getDuration(),
                    course.getBasicInfo().getLevel(),
                    course.getThemes(),
                    course.getTags()
            );
        }
    }

    /**
     * 상세 설명 정보
     */
    public record DescriptionInfo(
            String content,
            List<String> highlights
    ) {
        public static DescriptionInfo from(CourseDescription description) {
            if (description == null) {
                return new DescriptionInfo("", List.of());
            }
            return new DescriptionInfo(
                    description.getContent(),
                    description.getHighlights()
            );
        }
    }

    /**
     * 코스 플랜
     */
    public record CoursePlanDto(
            Long id,
            Long order,
            Long placeId,
            String title,
            String content
    ) {
        public static CoursePlanDto from(CoursePlans plan) {
            return new CoursePlanDto(
                    plan.getId(),
                    plan.getIsOrder(),
                    plan.getPlaceId(),
                    plan.getTitle(),
                    plan.getContent()
            );
        }
    }
}
