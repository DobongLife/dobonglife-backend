package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.dto.MyCourseResponse;
import com.umust.dobonglife.domain.course.application.port.in.GetCourseUseCase;
import com.umust.dobonglife.domain.course.application.port.out.LoadCoursePort;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.exception.CourseErrorCode;
import com.umust.dobonglife.domain.course.exception.CourseException;
import com.umust.dobonglife.domain.like.application.port.in.GetLikeUseCase;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseQueryService implements GetCourseUseCase {

    private final LoadCoursePort loadCoursePort;
    private final GetLikeUseCase getLikeUseCase;

    @Override
    public Course getCourse(Long courseId) {
        return loadCoursePort.findByIdAndActive(courseId)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND));
    }

    @Override
    public CursorResponse<CourseSummaryResponse> getAllCourses(Long userId, Theme theme, Long lastId, int size) {
        CursorResponse<CourseSummaryResponse> response = CursorUtils.toCursorResponse(
                loadCoursePort.findAllCourses(theme, lastId, size), c -> c);

        Set<Long> likedCourseIds = getLikeUseCase.getLikedTargetIds(userId, TargetType.COURSE);

        return CursorUtils.convert(response, c -> c.withLiked(likedCourseIds.contains(c.courseId())));
    }

    @Override
    public MyCourseResponse getMyCourses(Long userId, Long lastId, int size) {
        long totalCount = loadCoursePort.countByUserIdAndActive(userId);

        CursorResponse<CourseSummaryResponse> courses = CursorUtils.toCursorResponse(
                loadCoursePort.findMyCourses(userId, lastId, size), c -> c);

        return MyCourseResponse.of(totalCount, courses);
    }

    @Override
    public Map<Long, Course> getCoursesInBatch(List<Long> courseIds) {
        return loadCoursePort.findAllByIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));
    }
}
