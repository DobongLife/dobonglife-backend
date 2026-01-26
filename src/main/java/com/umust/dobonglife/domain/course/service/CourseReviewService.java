package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseReviewService {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final CourseLikeService courseLikeService;
    private final ReviewService reviewService;
    private final CoursePlansRepository coursePlansRepository;

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourse(Long userId, Long courseId) {
        Course course = courseRepository.findByIdWithDescription(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        boolean isRemoved = userService.validateOwner(userId, course.getUserId());
        boolean isFavorite = courseLikeService.isCourseFavorite(userId, courseId);

        List<CoursePlans> plans = coursePlansRepository
                .findByCourseIdOrderByIsOrder(courseId);

        CursorResponse<ReviewSummaryResponse> reviews = reviewService.getCourseReviews(courseId, userId, 10L, 3);
        return CourseDetailResponse.from(course, plans, reviews, isRemoved, isFavorite);
    }
}
