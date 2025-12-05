package com.umust.dobonglife.domain.place.infrasructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}