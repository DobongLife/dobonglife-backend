package com.umust.dobonglife.domain.place.infrasructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.entity.QCoursePlace;
import com.umust.dobonglife.domain.place.domain.entity.QPlace;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Place> findDistinctPlacesByThemeLimit3(CourseTheme theme) {

        QCoursePlace coursePlace = QCoursePlace.coursePlace;
        QPlace place = QPlace.place;

        return queryFactory
                .selectDistinct(place)
                .from(coursePlace)
                .join(coursePlace.place, place)
                .where(coursePlace.theme.eq(theme))
                .limit(3)
                .fetch();
    }
}
