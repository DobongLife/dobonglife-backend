package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.s3.S3Utils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CoursePlansRepository coursePlansRepository;
    private final S3Utils s3Utils;

    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findCoursesNoOffset(lastId, pageable);

        List<CourseSummaryResponse> content = courses.getContent()
                .stream()
                .map(CourseSummaryResponse::from)
                .collect(Collectors.toList());

        return new CursorResponse<>(content, courses.hasNext());
    }

    public CourseDetailResponse getCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        List<CoursePlans> plans = coursePlansRepository.findByCourseIdOrderByDateTime(courseId);
        return CourseDetailResponse.from(course, plans);
    }

    @Transactional
    public CourseResponse registerCourse(CreateCourseRequest request){
        List<String> images = s3Utils.uploadImages(request.imageUrls());

        Course course = Course.create(
                request.title(),
                request.subTitle(),
                request.themes(),
                request.duration(),
                request.level(),
                request.tags(),
                images,
                request.content(),
                request.meetingPlace(),
                request.contact(),
                request.cost(),
                request.maxNum(),
                request.ageLimit(),
                request.cancelPolicy(),
                request.weatherPolicy(),
                request.highlights(),
                request.exclusions(),
                request.inclusions()
        );

        Course savedCourse = courseRepository.save(course);

        request.plans().forEach(planRequest -> {
            CoursePlans plan = CoursePlans.create(
                    savedCourse.getId(),
                    planRequest.dateTime(),
                    planRequest.title(),
                    planRequest.content()
            );
            coursePlansRepository.save(plan);
        });

        return CourseResponse.from(savedCourse);
    }
}
