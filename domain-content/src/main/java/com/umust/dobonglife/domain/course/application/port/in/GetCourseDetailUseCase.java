package com.umust.dobonglife.domain.course.application.port.in;

import com.umust.dobonglife.domain.course.application.dto.CourseDetailResponse;

public interface GetCourseDetailUseCase {

    CourseDetailResponse getCourseDetail(Long courseId, Long userId, Long lastId, int size);
}
