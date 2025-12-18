package com.umust.dobonglife.domain.course.domain.repository;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import com.umust.dobonglife.domain.review.service.dto.ReviewStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
    @Query("SELECT c FROM Course c WHERE (:lastId IS NULL OR c.id < :lastId) ORDER BY c.id DESC")
    Slice<Course> findCoursesNoOffset(@Param("lastId") Long lastId, Pageable pageable);
}
