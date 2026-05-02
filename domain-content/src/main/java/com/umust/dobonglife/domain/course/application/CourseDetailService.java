package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseDetailResponse;
import com.umust.dobonglife.domain.course.application.port.in.GetCourseDetailUseCase;
import com.umust.dobonglife.domain.course.application.port.in.GetCourseUseCase;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.like.application.port.in.GetLikeUseCase;
import com.umust.dobonglife.domain.review.application.port.in.GetReviewUseCase;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseDetailService implements GetCourseDetailUseCase {

    private final GetCourseUseCase getCourseUseCase;
    private final GetLikeUseCase getLikeUseCase;
    private final GetReviewUseCase getReviewUseCase;

    @Override
    public CourseDetailResponse getCourseDetail(Long courseId, Long userId, Long lastId, int size) {
        Course course = getCourseUseCase.getCourse(courseId);

        boolean isLiked = getLikeUseCase.isLiked(userId, TargetType.COURSE, courseId);

        CursorResponse<ReviewSummaryResponse> reviews = getReviewUseCase.getReviews(TargetType.COURSE, courseId, lastId, size);

        return CourseDetailResponse.of(course, isLiked, reviews);
    }
}
