package com.umust.dobonglife.domain.course.domain.repository;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long>, CourseRepositoryCustom {
<<<<<<< Updated upstream
=======
    @Query(value = "SELECT * FROM course ORDER BY RAND()",
            countQuery = "SELECT count(*) FROM course",
            nativeQuery = true)
    Page<Course> findAllRandomOrder(Pageable pageable);
>>>>>>> Stashed changes
}
