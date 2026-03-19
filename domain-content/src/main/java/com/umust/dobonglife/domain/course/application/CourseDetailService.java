package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseDetailResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.review.application.ReviewService;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseDetailService {

    private final CourseService courseService;
    private final LikeService likeService;
    private final ReviewService reviewService;

    public CourseDetailResponse getCourseDetail(Long courseId, Long userId, Long lastId, int size) {
        Course course = courseService.getCourse(courseId);

        boolean isLiked = likeService.isLiked(userId, TargetType.COURSE, courseId);

        CursorResponse<ReviewSummaryResponse> reviews = reviewService.getReviews(TargetType.COURSE, courseId, lastId, size);

        return CourseDetailResponse.of(course, isLiked, reviews);
    }
}
