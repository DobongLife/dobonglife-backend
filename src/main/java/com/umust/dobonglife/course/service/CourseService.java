package com.umust.dobonglife.course.service;

import com.umust.dobonglife.course.domain.entity.Course;
import com.umust.dobonglife.course.domain.entity.CoursePlans;
import com.umust.dobonglife.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.course.exception.CourseException;
import com.umust.dobonglife.course.presentation.dto.response.CourseDetailResponse;
import com.umust.dobonglife.course.presentation.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.course.domain.repository.CourseRepository;
import com.umust.dobonglife.global.common.response.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CoursePlansRepository coursePlansRepository;
    @Transactional
    public Page<CourseSummaryResponse> getCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseRepository.findAllRandomOrder(pageable);
        return courses.map(CourseSummaryResponse::from);
    }

    public CourseDetailResponse getCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseException(ErrorCode.INVALID_COURSE_ID));

        List<CoursePlans> plans = coursePlansRepository.findByCourseIdOrderByDateTime(courseId);
        return CourseDetailResponse.from(course, plans);
    }
}
