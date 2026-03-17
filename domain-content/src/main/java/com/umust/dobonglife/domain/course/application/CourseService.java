package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.exception.CourseErrorCode;
import com.umust.dobonglife.domain.course.exception.CourseException;
import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {
    private final CourseRepository courseRepository;
    private final LikeService likeService;

    public Course getCourse(Long courseId) {
        return courseRepository.findByIdAndStatus(courseId, BaseStatus.ACTIVE)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND));
    }

    public CursorResponse<CourseSummaryResponse> getAllCourses(Long userId, Long lastId, int size) {
        CursorResponse<CourseSummaryResponse> response = CursorUtils.toCursorResponse(
                courseRepository.findAllCourses(lastId, size), c -> c);

        Set<Long> likedCourseIds = likeService.getLikedTargetIds(userId, TargetType.COURSE);

        return CursorUtils.convert(response, c -> c.withLiked(likedCourseIds.contains(c.courseId())));
    }
}
