package com.umust.dobonglife.domain.course.infrastructure.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umust.dobonglife.domain.course.domain.entity.QCourse.course;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Course> findByTheme(CourseTheme theme){

        return queryFactory
                .selectDistinct(course)
                .from(course)
                .where(course.themes.any().eq(theme))
                .orderBy(course.id.desc())
                .fetch();
    }

    @Override
    public List<Course> findDistinctPlacesByThemeLimit3(CourseTheme theme) {

        return queryFactory
                .selectDistinct(course)
                .from(course)
                .where(course.themes.contains(theme))
                .orderBy(course.id.desc())
                .limit(3)
                .fetch();
    }
}
