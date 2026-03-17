package com.umust.dobonglife.domain.course.domain.repository.custom;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import org.springframework.data.domain.Slice;

public interface CourseRepositoryCustom {

    Slice<CourseSummaryResponse> findAllCourses(Long lastId, int size);
}
