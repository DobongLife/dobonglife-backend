package com.umust.dobonglife.domain.course.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.umust.dobonglife.domain.course.domain.entity.QCourse.course;
import static com.umust.dobonglife.domain.course.domain.entity.QCourseTheme.courseTheme;

@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<CourseSummaryResponse> findAllCourses(Theme theme, Long lastId, int size) {
        JPAQuery<CourseSummaryResponse> query = queryFactory
                .select(Projections.constructor(CourseSummaryResponse.class,
                        course.id,
                        course.title,
                        course.subTitle,
                        course.level.stringValue(),
                        course.duration,
                        course.thumbnail.imageUrl,
                        course.averageRating,
                        course.reviewCount,
                        Expressions.constant(false)
                ))
                .from(course)
                .leftJoin(course.thumbnail);

        if (theme != null) {
            query.join(course.themes, courseTheme)
                    .on(courseTheme.theme.eq(theme));
        }

        List<CourseSummaryResponse> content = query
                .where(
                        course.status.eq(BaseStatus.ACTIVE),
                        lastIdCondition(lastId)
                )
                .orderBy(course.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content = content.subList(0, size);
        }

        return new SliceImpl<>(content, PageRequest.of(0, size), hasNext);
    }

    @Override
    public Slice<CourseSummaryResponse> findMyCourses(Long userId, Long lastId, int size) {
        List<CourseSummaryResponse> content = queryFactory
                .select(Projections.constructor(CourseSummaryResponse.class,
                        course.id,
                        course.title,
                        course.subTitle,
                        course.level.stringValue(),
                        course.duration,
                        course.thumbnail.imageUrl,
                        course.averageRating,
                        course.reviewCount,
                        Expressions.constant(false)
                ))
                .from(course)
                .leftJoin(course.thumbnail)
                .where(
                        course.userId.eq(userId),
                        course.status.eq(BaseStatus.ACTIVE),
                        lastIdCondition(lastId)
                )
                .orderBy(course.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content = content.subList(0, size);
        }

        return new SliceImpl<>(content, PageRequest.of(0, size), hasNext);
    }

    private BooleanExpression lastIdCondition(Long lastId) {
        return lastId != null ? course.id.lt(lastId) : null;
    }
}
