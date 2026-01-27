package com.umust.dobonglife.domain.course.domain.repository;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import com.umust.dobonglife.domain.review.service.dto.ReviewStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
    @Query("SELECT c FROM Course c " +
            "WHERE (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    Slice<Course> findCoursesNoOffset(@Param("lastId") Long lastId, Pageable pageable);

    // 목록 조회 - description 제외
    @Query("SELECT c FROM Course c")
    Page<Course> findAllForList(Pageable pageable);

    // 상세 조회 - description 포함
    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.description " +
            "WHERE c.id = :id")
    Optional<Course> findByIdWithDescription(@Param("id") Long id);

    // 테마별 조회 - description 제외
    @Query("SELECT c FROM Course c " +
            "JOIN c.themes t " +
            "WHERE t = :theme " +
            "AND (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    Slice<Course> findByThemeNoOffset(
            @Param("theme") CourseTheme theme,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("SELECT c FROM Course c " +
            "WHERE c.userId = :userId " +
            "AND (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    Slice<Course> findMyCoursesNoOffset(Long userId, Long lastId, Pageable pageable);

    Long countByUserId(Long userId);
}
