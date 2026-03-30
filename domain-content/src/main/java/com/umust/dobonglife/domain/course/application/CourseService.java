package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.dto.CreateCourseRequest;
import com.umust.dobonglife.domain.course.application.dto.MyCourseResponse;
import com.umust.dobonglife.domain.course.application.dto.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.application.port.in.GetCourseUseCase;
import com.umust.dobonglife.domain.course.application.port.in.ManageCourseUseCase;
import com.umust.dobonglife.domain.course.application.port.out.LoadCoursePort;
import com.umust.dobonglife.domain.course.application.port.out.SaveCoursePort;
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
public class CourseService implements GetCourseUseCase, ManageCourseUseCase {

    private final LoadCoursePort loadCoursePort;
    private final SaveCoursePort saveCoursePort;
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
    @Transactional
    public CourseRegisterResponse createCourse(Long userId, CreateCourseRequest request) {
        Course course = request.toEntity(userId);
        saveCoursePort.save(course);
        return CourseRegisterResponse.from(course.getId());
    }

    @Override
    @Transactional
    public CourseRegisterResponse updateCourse(Long userId, Long courseId, UpdateCourseRequest request) {
        Course course = getCourse(courseId);

        if (!course.isOwner(userId)) {
            throw new CourseException(CourseErrorCode.NOT_OWNER);
        }

        course.update(
                request.title(),
                request.subTitle(),
                request.level(),
                request.duration(),
                request.content(),
                request.newImageUrls(),
                request.deleteImageUrls(),
                request.toCourseThemes(),
                request.toCoursePlans(),
                request.toCourseTags()
        );

        return CourseRegisterResponse.from(course.getId());
    }

    @Override
    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        Course course = getCourse(courseId);

        if (!course.isOwner(userId)) {
            throw new CourseException(CourseErrorCode.NOT_OWNER);
        }

        course.deactivate();
    }

    @Override
    public Map<Long, Course> getCoursesInBatch(List<Long> courseIds) {
        return loadCoursePort.findAllByIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));
    }

    @Override
    @Transactional
    public void addReview(Long courseId, Double rating) {
        saveCoursePort.addReview(courseId, rating);
    }

    @Override
    @Transactional
    public void removeReview(Long courseId, Double rating) {
        saveCoursePort.removeReview(courseId, rating);
    }
}
