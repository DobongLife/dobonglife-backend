package com.umust.dobonglife.domain.course.infrastructure.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.umust.dobonglife.global.common.model.BaseStatus;

import static com.umust.dobonglife.domain.course.domain.entity.QCourse.course;
import static com.umust.dobonglife.domain.courseLike.domain.entity.QCourseLike.courseLike;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Course> findByTheme(CourseTheme theme){

        return queryFactory
                .selectDistinct(course)
                .from(course)
                .where(
                        course.status.eq(BaseStatus.ACTIVE),
                        course.themes.any().eq(theme)
                )
                .orderBy(course.id.desc())
                .fetch();
    }

    @Override
    public List<Course> findDistinctPlacesByThemeLimit3(CourseTheme theme) {

        return queryFactory
                .selectDistinct(course)
                .from(course)
                .where(
                        course.status.eq(BaseStatus.ACTIVE),
                        course.themes.contains(theme)
                )
                .orderBy(course.id.desc())
                .limit(3)
                .fetch();
    }

    @Override
    public Slice<Course> findLikedCourses(Long userId, Long lastId, Pageable pageable) {
        List<Course> contents = queryFactory
                .select(course)
                .from(course)
                .join(courseLike).on(courseLike.courseId.eq(course.id))
                .where(
                        course.status.eq(BaseStatus.ACTIVE),
                        courseLike.userId.eq(userId),
                        ltCourseId(lastId)
                )
                .orderBy(course.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, contents);
    }

    private BooleanExpression ltCourseId(Long lastId) {
        return lastId == null ? null : course.id.lt(lastId);
    }

    private Slice<Course> checkLastPage(Pageable pageable, List<Course> results) {
        boolean hasNext = false;

        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
