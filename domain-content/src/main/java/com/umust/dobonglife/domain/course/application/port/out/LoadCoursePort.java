package com.umust.dobonglife.domain.course.application.port.out;

import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface LoadCoursePort {

    Optional<Course> findByIdAndActive(Long courseId);

    long countByUserIdAndActive(Long userId);

    Slice<CourseSummaryResponse> findAllCourses(Theme theme, Long lastId, int size);

    Slice<CourseSummaryResponse> findMyCourses(Long userId, Long lastId, int size);

    List<Course> findAllByIds(List<Long> courseIds);
}
