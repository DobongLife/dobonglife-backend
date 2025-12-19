package com.umust.dobonglife.domain.course.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.QCourse;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

}
