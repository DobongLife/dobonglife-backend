package com.umust.dobonglife.domain.course.application.port.in;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.dto.MyCourseResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.common.response.CursorResponse;

import java.util.List;
import java.util.Map;

public interface GetCourseUseCase {

    Course getCourse(Long courseId);

    CursorResponse<CourseSummaryResponse> getAllCourses(Long userId, Theme theme, Long lastId, int size);

    MyCourseResponse getMyCourses(Long userId, Long lastId, int size);

    Map<Long, Course> getCoursesInBatch(List<Long> courseIds);
}
