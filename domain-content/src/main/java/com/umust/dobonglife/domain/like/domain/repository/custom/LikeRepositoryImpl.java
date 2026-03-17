package com.umust.dobonglife.domain.like.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.umust.dobonglife.domain.course.domain.entity.QCourse.course;
import static com.umust.dobonglife.domain.course.domain.entity.QCourseTheme.courseTheme;
import static com.umust.dobonglife.domain.like.domain.entity.QLike.like;
import static com.umust.dobonglife.domain.place.domain.entity.QPlace.place;
import static com.umust.dobonglife.domain.place.domain.entity.QPlaceTheme.placeTheme;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MyLikedPlaceResponse> findMyLikedPlaces(Long userId, Long lastId, int size) {
        List<MyLikedPlaceResponse> content = queryFactory
                .select(Projections.constructor(MyLikedPlaceResponse.class,
                        place.id,
                        place.name,
                        place.category.stringValue(),
                        place.thumbnail.imageUrl,
                        place.averageRating,
                        place.reviewCount,
                        Expressions.constant(true),
                        place.latitude,
                        place.longitude,
                        Expressions.nullExpression(List.class),
                        place.status
                ))
                .from(like)
                .join(place).on(like.targetId.eq(place.id))
                .leftJoin(place.thumbnail)
                .where(
                        like.userId.eq(userId),
                        like.targetType.eq(TargetType.PLACE),
                        lastIdCondition(lastId)
                )
                .orderBy(like.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content = content.subList(0, size);
        }

        List<Long> placeIds = content.stream()
                .map(MyLikedPlaceResponse::placeId)
                .toList();

        Map<Long, List<String>> themesMap = fetchThemes(placeIds);

        List<MyLikedPlaceResponse> result = content.stream()
                .map(r -> new MyLikedPlaceResponse(
                        r.placeId(), r.placeName(), r.category(), r.thumbnailUrl(),
                        r.averageRating(), r.reviewCount(), r.isLiked(),
                        r.latitude(), r.longitude(),
                        themesMap.getOrDefault(r.placeId(), List.of()),
                        r.status()
                ))
                .toList();

        return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);
    }

    private Map<Long, List<String>> fetchThemes(List<Long> placeIds) {
        if (placeIds.isEmpty()) return Map.of();

        return queryFactory
                .select(place.id, placeTheme.theme.stringValue())
                .from(place)
                .join(place.themes, placeTheme)
                .where(place.id.in(placeIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(place.id),
                        Collectors.mapping(
                                tuple -> tuple.get(placeTheme.theme.stringValue()),
                                Collectors.toList()
                        )
                ));
    }

    @Override
    public Slice<MyLikedCourseResponse> findMyLikedCourses(Long userId, Long lastId, int size) {
        List<MyLikedCourseResponse> content = queryFactory
                .select(Projections.constructor(MyLikedCourseResponse.class,
                        course.id,
                        course.title,
                        course.subTitle,
                        course.level.stringValue(),
                        course.duration,
                        course.thumbnail.imageUrl,
                        course.averageRating,
                        course.reviewCount,
                        Expressions.constant(true),
                        Expressions.nullExpression(List.class),
                        course.status
                ))
                .from(like)
                .join(course).on(like.targetId.eq(course.id))
                .leftJoin(course.thumbnail)
                .where(
                        like.userId.eq(userId),
                        like.targetType.eq(TargetType.COURSE),
                        lastIdCondition(lastId)
                )
                .orderBy(like.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content = content.subList(0, size);
        }

        List<Long> courseIds = content.stream()
                .map(MyLikedCourseResponse::courseId)
                .toList();

        Map<Long, List<String>> themesMap = fetchCourseThemes(courseIds);

        List<MyLikedCourseResponse> result = content.stream()
                .map(r -> new MyLikedCourseResponse(
                        r.courseId(), r.title(), r.subTitle(), r.level(),
                        r.duration(), r.thumbnailUrl(), r.averageRating(),
                        r.reviewCount(), r.isLiked(),
                        themesMap.getOrDefault(r.courseId(), List.of()),
                        r.status()
                ))
                .toList();

        return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);
    }

    private Map<Long, List<String>> fetchCourseThemes(List<Long> courseIds) {
        if (courseIds.isEmpty()) return Map.of();

        return queryFactory
                .select(course.id, courseTheme.theme.stringValue())
                .from(course)
                .join(course.themes, courseTheme)
                .where(course.id.in(courseIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(course.id),
                        Collectors.mapping(
                                tuple -> tuple.get(courseTheme.theme.stringValue()),
                                Collectors.toList()
                        )
                ));
    }

    private BooleanExpression lastIdCondition(Long lastId) {
        return lastId != null ? like.id.lt(lastId) : null;
    }
}
