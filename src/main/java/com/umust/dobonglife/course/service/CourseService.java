package com.umust.dobonglife.course.service;

import com.umust.dobonglife.course.domain.entity.Course;
import com.umust.dobonglife.course.presentation.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.course.domain.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    @Transactional
    public Page<CourseSummaryResponse> getCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseRepository.findAllRandomOrder(pageable);
        return courses.map(CourseSummaryResponse::from);
    }
}
