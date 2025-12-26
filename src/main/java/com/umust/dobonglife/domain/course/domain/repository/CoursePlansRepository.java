package com.umust.dobonglife.domain.course.domain.repository;

import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoursePlansRepository extends JpaRepository<CoursePlans, Long> {
    @Query("SELECT c FROM CoursePlans c WHERE c.id = :courseId")
    List<CoursePlans> findByCourseIdOrderByDateTime(@Param("courseId") Long courseId);
}
