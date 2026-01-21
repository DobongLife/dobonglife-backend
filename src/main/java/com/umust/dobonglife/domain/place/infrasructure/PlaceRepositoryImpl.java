package com.umust.dobonglife.domain.place.infrasructure;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.querydsl.jpa.JPAExpressions.selectOne;
import static com.umust.dobonglife.domain.place.domain.entity.QPlace.place;
import static com.umust.dobonglife.domain.place.domain.entity.QPlaceLike.placeLike;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PlaceSummaryResponse> findPlaceSummaries(Long userId) {

        // 1) place + liked (컬렉션 조인 X)
        BooleanExpression likedExpr = likedExpr(userId);

        List<Tuple> baseRows = queryFactory
                .select(place, likedExpr)
                .from(place)
                .orderBy(place.id.desc())
                .fetch();

        if (baseRows.isEmpty()) return List.of();

        // placeIds 추출
        List<Long> placeIds = baseRows.stream()
                .map(t -> t.get(place).getId())
                .distinct()
                .toList();


        EnumPath<CourseTheme> theme = Expressions.enumPath(CourseTheme.class, "theme");

        List<Tuple> themeRows = queryFactory
                .select(place.id, theme)
                .from(place)
                .join(place.themes, theme)
                .where(place.id.in(placeIds))
                .fetch();

        Map<Long, List<CourseTheme>> themesMap = new HashMap<>();
        for (Tuple tr : themeRows) {
            Long pid = tr.get(place.id);
            CourseTheme th = tr.get(theme);
            themesMap.computeIfAbsent(pid, k -> new ArrayList<>()).add(th);
        }

        return baseRows.stream()
                .map(t -> {
                    Place p = t.get(place);
                    Boolean liked = t.get(likedExpr);
                    List<CourseTheme> themes = themesMap.getOrDefault(p.getId(), List.of());
                    return PlaceSummaryResponse.from(p, liked, themes);
                })
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

    @Override
    public Slice<Place> findLikedPlaceSummaries(Long userId, Long lastId, Pageable pageable) {
        List<Place> contents = queryFactory
                .select(place)
                .from(placeLike)
                .join(place).on(placeLike.place.id.eq(place.id))
                .where(
                        placeLike.user.id.eq(userId),
                        placeLike.status.eq(BaseStatus.ACTIVE),
                        ltPlaceId(lastId)
                )
                .orderBy(place.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, contents);
    }

    private BooleanExpression ltPlaceId(Long lastId) {
        return lastId == null ? null : place.id.lt(lastId);
    }

    private Slice<Place> checkLastPage(Pageable pageable, List<Place> results) {
        boolean hasNext = false;
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }
}
