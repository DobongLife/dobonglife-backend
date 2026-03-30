package com.umust.dobonglife.domain.course.application;

import com.umust.dobonglife.domain.course.application.dto.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.application.dto.CreateCourseRequest;
import com.umust.dobonglife.domain.course.application.dto.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.application.port.in.ManageCourseUseCase;
import com.umust.dobonglife.domain.course.application.port.out.LoadCoursePort;
import com.umust.dobonglife.domain.course.application.port.out.SaveCoursePort;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.exception.CourseErrorCode;
import com.umust.dobonglife.domain.course.exception.CourseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseCommandService implements ManageCourseUseCase {

    private final LoadCoursePort loadCoursePort;
    private final SaveCoursePort saveCoursePort;

    @Override
    public CourseRegisterResponse createCourse(Long userId, CreateCourseRequest request) {
        Course course = request.toEntity(userId);
        saveCoursePort.save(course);
        return CourseRegisterResponse.from(course.getId());
    }

    @Override
    public CourseRegisterResponse updateCourse(Long userId, Long courseId, UpdateCourseRequest request) {
        Course course = loadCoursePort.findByIdAndActive(courseId)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND));

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
    public void deleteCourse(Long userId, Long courseId) {
        Course course = loadCoursePort.findByIdAndActive(courseId)
                .orElseThrow(() -> new CourseException(CourseErrorCode.COURSE_NOT_FOUND));

        if (!course.isOwner(userId)) {
            throw new CourseException(CourseErrorCode.NOT_OWNER);
        }

        course.deactivate();
    }

    @Override
    public void addReview(Long courseId, Double rating) {
        saveCoursePort.addReview(courseId, rating);
    }

    @Override
    public void removeReview(Long courseId, Double rating) {
        saveCoursePort.removeReview(courseId, rating);
    }
}
