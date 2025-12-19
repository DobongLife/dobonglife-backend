package com.umust.dobonglife.domain.course.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.ReviewSummary;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;

import java.util.List;

/**
 * 코스 목록 조회 응답
 * 상세 설명 제외, 필수 정보만 포함
 * 성능 최적화: description 테이블 조인 불필요하게 적용
 */
public record CourseListResponse(
        Long id,
        String title,
        String subTitle,
        Double duration,
        CourseLevel level,
        List<CourseTheme> themes,
        List<String> tags,
        String thumbnailUrl, // 첫 번째 이미지만
        ReviewSummary reviewSummary,
        String cost,
        String ageLimit
) {
    public static CourseListResponse from(Course course) {
        return new CourseListResponse(
                course.getId(),
                course.getBasicInfo().getTitle(),
                course.getBasicInfo().getSubTitle(),
                course.getBasicInfo().getDuration(),
                course.getBasicInfo().getLevel(),
                course.getThemes(),
                course.getTags(),
                course.getImageUrls().isEmpty() ? null : course.getImageUrls().get(0),
                ReviewSummary.from(course),
                course.getOperationInfo().getCost(),
                course.getOperationInfo().getAgeLimit()
        );
    }
}
