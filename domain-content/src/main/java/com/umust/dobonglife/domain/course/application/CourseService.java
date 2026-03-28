package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.dto.CreateCourseRequest;
import com.umust.dobonglife.domain.course.application.dto.MyCourseResponse;
import com.umust.dobonglife.domain.course.application.dto.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.exception.CourseErrorCode;
import com.umust.dobonglife.domain.course.exception.CourseException;
import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
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

    public CursorResponse<CourseSummaryResponse> getAllCourses(Long userId, Theme theme, Long lastId, int size) {
        CursorResponse<CourseSummaryResponse> response = CursorUtils.toCursorResponse(
                courseRepository.findAllCourses(theme, lastId, size), c -> c);

        Set<Long> likedCourseIds = likeService.getLikedTargetIds(userId, TargetType.COURSE);

        return CursorUtils.convert(response, c -> c.withLiked(likedCourseIds.contains(c.courseId())));
    }

    public MyCourseResponse getMyCourses(Long userId, Long lastId, int size) {
        long totalCount = courseRepository.countByUserIdAndStatus(userId, BaseStatus.ACTIVE);

        CursorResponse<CourseSummaryResponse> courses = CursorUtils.toCursorResponse(
                courseRepository.findMyCourses(userId, lastId, size), c -> c);

        return MyCourseResponse.of(totalCount, courses);
    }

    @Transactional
    public CourseRegisterResponse createCourse(Long userId, CreateCourseRequest request) {
        Course course = request.toEntity(userId);
        courseRepository.save(course);
        return CourseRegisterResponse.from(course.getId());
    }

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

    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        Course course = getCourse(courseId);

        if (!course.isOwner(userId)) {
            throw new CourseException(CourseErrorCode.NOT_OWNER);
        }

        course.deactivate();
    }
}
