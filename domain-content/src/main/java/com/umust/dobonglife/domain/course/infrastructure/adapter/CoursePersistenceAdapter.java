package com.umust.dobonglife.domain.course.infrastructure.adapter;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.port.out.LoadCoursePort;
import com.umust.dobonglife.domain.course.application.port.out.SaveCoursePort;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.infrastructure.jpa.CourseRepository;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoursePersistenceAdapter implements LoadCoursePort, SaveCoursePort {

    private final CourseRepository courseRepository;

    // ── LoadCoursePort ──

    @Override
    public Optional<Course> findByIdAndActive(Long courseId) {
        return courseRepository.findByIdAndStatus(courseId, BaseStatus.ACTIVE);
    }

    @Override
    public long countByUserIdAndActive(Long userId) {
        return courseRepository.countByUserIdAndStatus(userId, BaseStatus.ACTIVE);
    }

    @Override
    public Slice<CourseSummaryResponse> findAllCourses(Theme theme, Long lastId, int size) {
        return courseRepository.findAllCourses(theme, lastId, size);
    }

    @Override
    public Slice<CourseSummaryResponse> findMyCourses(Long userId, Long lastId, int size) {
        return courseRepository.findMyCourses(userId, lastId, size);
    }

    @Override
    public List<Course> findAllByIds(List<Long> courseIds) {
        return courseRepository.findAllById(courseIds);
    }

    // ── SaveCoursePort ──

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void addReview(Long courseId, Double rating) {
        courseRepository.addReview(courseId, rating);
    }

    @Override
    public void removeReview(Long courseId, Double rating) {
        courseRepository.removeReview(courseId, rating);
    }
}
