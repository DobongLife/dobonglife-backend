package com.umust.dobonglife.domain.place.infrasructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.umust.dobonglife.domain.place.domain.entity.QPlace.place;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Place> findDistinctPlacesByThemeLimit3(CourseTheme theme) {

        return queryFactory
                .selectDistinct(place)
                .from(place)
                .where(place.themes.eq(theme))
                .limit(3)
                .fetch();
    }
}
