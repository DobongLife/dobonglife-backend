package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.ReviewSummary;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CourseDescription;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 코스 상세 조회 응답
 * 모든 정보 포함
 * description은 fetch join으로 가져와야 함
 */
public record CourseDetailResponse(
        Long id,
        BasicInfo basicInfo,
        OperationInfo operationInfo,
        PolicyInfo policyInfo,
        DescriptionInfo descriptionInfo,
        ReviewSummary reviewSummary,
        List<String> imageUrls,
        List<CoursePlanDto> plans
) {
    public static CourseDetailResponse from(Course course, List<CoursePlans> plans) {
        return new CourseDetailResponse(
                course.getId(),
                BasicInfo.from(course),
                OperationInfo.from(course),
                PolicyInfo.from(course),
                DescriptionInfo.from(course.getDescription()),
                ReviewSummary.from(course),
                course.getImageUrls(),
                plans.stream().map(CoursePlanDto::from).toList()
        );
    }



    /**
     * 기본 정보
     */
    public record BasicInfo(
            String title,
            String subTitle,
            Double duration,
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
     * 운영 정보
     */
    public record OperationInfo(
            String meetingPlace,
            String contact,
            String cost,
            Integer maxNum,
            String ageLimit
    ) {
        public static OperationInfo from(Course course) {
            return new OperationInfo(
                    course.getOperationInfo().getMeetingPlace(),
                    course.getOperationInfo().getContact(),
                    course.getOperationInfo().getCost(),
                    course.getOperationInfo().getMaxNum(),
                    course.getOperationInfo().getAgeLimit()
            );
        }
    }

    /**
     * 정책 정보
     */
    public record PolicyInfo(
            String cancelPolicy,
            String weatherPolicy
    ) {
        public static PolicyInfo from(Course course) {
            return new PolicyInfo(
                    course.getPolicyInfo().getCancelPolicy(),
                    course.getPolicyInfo().getWeatherPolicy()
            );
        }
    }

    /**
     * 상세 설명 정보
     */
    public record DescriptionInfo(
            String content,
            List<String> highlights,
            List<String> inclusions,
            List<String> exclusions
    ) {
        public static DescriptionInfo from(CourseDescription description) {
            if (description == null) {
                return new DescriptionInfo("", List.of(), List.of(), List.of());
            }
            return new DescriptionInfo(
                    description.getContent(),
                    description.getHighlights(),
                    description.getInclusions(),
                    description.getExclusions()
            );
        }
    }

    /**
     * 코스 플랜
     */
    public record CoursePlanDto(
            Long id,
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
