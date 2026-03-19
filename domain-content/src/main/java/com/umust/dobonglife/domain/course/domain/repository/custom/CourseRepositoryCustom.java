package com.umust.dobonglife.domain.course.domain.repository.custom;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import org.springframework.data.domain.Slice;

public interface CourseRepositoryCustom {

    Slice<CourseSummaryResponse> findAllCourses(Theme theme, Long lastId, int size);

    Slice<CourseSummaryResponse> findMyCourses(Long userId, Long lastId, int size);
}
