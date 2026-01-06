package com.umust.dobonglife.domain.course.infrastructure.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseResponse;
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

    @Override
    public List<CourseResponse> findCourseResponsesByTheme(CourseTheme theme) {

        List<Tuple> base = queryFactory
                .select(
                        course.id,
                        course.basicInfo.title,
                        course.basicInfo.subTitle,
                        course.basicInfo.level,
                        course.reviewStats.reviewCount,
                        course.reviewStats.averageRating
                )
                .from(course)
                .where(course.themes.any().eq(theme))
                .orderBy(course.id.desc())
                .fetch();

        if (base.isEmpty()) return List.of();

        List<Long> courseIds = base.stream()
                .map(t -> t.get(course.id))
                .toList();

        StringPath tag = Expressions.stringPath("tag");
        List<Tuple> tagRows = queryFactory
                .select(course.id, tag)
                .from(course)
                .leftJoin(course.tags, tag)
                .where(course.id.in(courseIds))
                .fetch();

        Map<Long, List<String>> tagsMap = new HashMap<>();
        for (Tuple row : tagRows) {
            Long courseId = row.get(course.id);
            String v = row.get(tag);
            if (v == null) continue;
            tagsMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(v);
        }

        StringPath image = Expressions.stringPath("image");
        List<Tuple> imageRows = queryFactory
                .select(course.id, image)
                .from(course)
                .leftJoin(course.imageUrls, image)
                .where(course.id.in(courseIds))
                .fetch();

        Map<Long, String> thumbnailMap = new HashMap<>();
        for (Tuple row : imageRows) {
            Long courseId = row.get(course.id);
            String url = row.get(image);
            if (url == null) continue;
            thumbnailMap.putIfAbsent(courseId, url);
        }

        List<CourseResponse> result = new ArrayList<>(base.size());
        for (Tuple t : base) {
            Long courseId = t.get(course.id);

            result.add(new CourseResponse(
                    courseId,
                    t.get(course.basicInfo.title),
                    t.get(course.basicInfo.subTitle),
                    thumbnailMap.get(courseId),
                    tagsMap.getOrDefault(courseId, List.of()),
                    t.get(course.basicInfo.level),
                    t.get(course.reviewStats.reviewCount),
                    t.get(course.reviewStats.averageRating)
            ));
        }

        return result;
    }
}
