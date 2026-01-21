package com.umust.dobonglife.domain.place.infrasructure;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceRepositoryCustom;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.selectOne;
import static com.umust.dobonglife.domain.place.domain.entity.QPlace.place;
import static com.umust.dobonglife.domain.place.domain.entity.QPlaceLike.placeLike;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaceSummaryResponse> findPlaceSummariesByTheme(CourseTheme theme, Integer size) {

        JPAQuery<Tuple> query = queryFactory
                .select(
                        place.id,
                        place.name,
                        place.thumbnailUrl,
                        place.averageRating,
                        place.reviewCount
                )
                .from(place)
                .where(place.themes.any().eq(theme))
                .orderBy(place.id.desc());

        if (size != null) query.limit(size);

        List<Tuple> rows = query.fetch();
        return rows.stream().map(this::toSummary).toList();
    }

    @Override
    public List<PlaceSummaryResponse> findPlaceSummaries(Long userId) {

        BooleanExpression liked = likedExpr(userId);

        List<Tuple> rows = queryFactory
                .select(place, liked)
                .from(place)
                .leftJoin(place.themes).fetchJoin()
                .distinct()
                .orderBy(place.id.desc())
                .fetch();

        return rows.stream()
                .map(t -> PlaceSummaryResponse.from(
                        t.get(place),
                        t.get(liked)
                ))
                .toList();
    }

    @Override
    public List<PlaceSummaryResponse> findLikedPlaceSummaries(Long userId) {

        BooleanExpression liked = likedExpr(userId);

        List<Tuple> rows = queryFactory
                .select(place, liked)
                .from(place)
                .leftJoin(place.themes).fetchJoin()
                .where(liked)
                .distinct()
                .orderBy(place.id.desc())
                .fetch();

        return rows.stream()
                .map(t -> PlaceSummaryResponse.from(
                        t.get(place),
                        t.get(liked)
                ))
                .toList();
    }

    private BooleanExpression likedExpr(Long userId) {
        return JPAExpressions
                .selectOne()
                .from(placeLike)
                .where(
                        placeLike.place.eq(place),
                        placeLike.user.id.eq(userId),
                        placeLike.status.eq(BaseStatus.ACTIVE)
                )
                .exists();
    }

    private PlaceSummaryResponse toSummary(Tuple t) {
        return PlaceSummaryResponse.builder()
                .placeId(t.get(place.id))
                .placeName(t.get(place.name))
                .thumbnailUrl(t.get(place.imageUrls.any()))
                .averageRating(t.get(place.averageRating))
                .reviewCount(t.get(place.reviewCount))
                .build();
    }
}
