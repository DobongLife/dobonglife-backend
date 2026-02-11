package com.umust.dobonglife.domain.course.domain.repository;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoursePlansRepository extends JpaRepository<CoursePlans, Long> {
    @Query("SELECT c FROM CoursePlans c WHERE c.course.id = :courseId ORDER BY c.isOrder ASC")
    List<CoursePlans> findByCourseIdOrderByIsOrder(@Param("courseId") Long courseId);

    @Modifying
    @Query("delete from CoursePlans cp where cp.course = :course")
    void deleteByCourseCustom(@Param("course") Course course);

    void deleteByCourseId(Long courseId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CoursePlans p SET p.placeId = null WHERE p.placeId = :placeId")
    void nullifyPlaceId(@Param("placeId") Long placeId);
}
