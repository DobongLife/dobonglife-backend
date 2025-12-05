package com.umust.dobonglife.domain.place.infrasructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import com.umust.dobonglife.global.common.model.BaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.umust.dobonglife.domain.place.domain.entity.QPlace.place;
import static com.umust.dobonglife.domain.place.domain.entity.QPlaceLike.placeLike;

@Repository
@RequiredArgsConstructor
public class PlaceLikeRepositoryImpl implements PlaceLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PlaceLike> findByUserIdAndPlaceId(Long userId, Long placeId) {

        PlaceLike result = queryFactory
                .selectFrom(placeLike)
                .where(
                        placeLike.user.id.eq(userId),
                        placeLike.place.id.eq(placeId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Place> findLikedPlacesByUserId(Long userId) {
        return queryFactory
                .select(place)
                .from(placeLike)
                .join(placeLike.place, place).fetchJoin()
                .where(
                        placeLike.user.id.eq(userId),
                        placeLike.status.eq(BaseStatus.ACTIVE)
                )
                .fetch();
    }
}