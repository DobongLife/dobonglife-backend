package com.umust.dobonglife.domain.course.infrastructure.adapter;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.port.out.LoadCoursePort;
import com.umust.dobonglife.domain.course.application.port.out.SaveCoursePort;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.infrastructure.jpa.CourseJpaRepository;
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

    private final CourseJpaRepository courseJpaRepository;

    // ── LoadCoursePort ──

    @Override
    public Optional<Course> findByIdAndActive(Long courseId) {
        return courseJpaRepository.findByIdAndStatus(courseId, BaseStatus.ACTIVE);
    }

    @Override
    public long countByUserIdAndActive(Long userId) {
        return courseJpaRepository.countByUserIdAndStatus(userId, BaseStatus.ACTIVE);
    }

    @Override
    public Slice<CourseSummaryResponse> findAllCourses(Theme theme, Long lastId, int size) {
        return courseJpaRepository.findAllCourses(theme, lastId, size);
    }

    @Override
    public Slice<CourseSummaryResponse> findMyCourses(Long userId, Long lastId, int size) {
        return courseJpaRepository.findMyCourses(userId, lastId, size);
    }

    @Override
    public List<Course> findAllByIds(List<Long> courseIds) {
        return courseJpaRepository.findAllById(courseIds);
    }

    // ── SaveCoursePort ──

    @Override
    public Course save(Course course) {
        return courseJpaRepository.save(course);
    }

    @Override
    public void addReview(Long courseId, Double rating) {
        courseJpaRepository.addReview(courseId, rating);
    }

    @Override
    public void removeReview(Long courseId, Double rating) {
        courseJpaRepository.removeReview(courseId, rating);
    }
}
