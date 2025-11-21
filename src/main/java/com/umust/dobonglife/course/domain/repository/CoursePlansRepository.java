package com.umust.dobonglife.course.domain.repository;

import com.umust.dobonglife.course.domain.entity.CoursePlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoursePlansRepository extends JpaRepository<CoursePlans, Integer> {
    @Query("SELECT c FROM Course c WHERE c.id = :courseId")
    List<CoursePlans> findByCourseIdOrderByDateTime(@Param("courseId") Integer courseId);
}
