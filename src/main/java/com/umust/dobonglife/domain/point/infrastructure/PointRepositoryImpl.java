package com.umust.dobonglife.domain.point.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.repository.custom.PointRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.umust.dobonglife.domain.point.domain.entity.QPoint.point;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Point> findMyPoint(Long userId) {

        return queryFactory
                .selectFrom(point)
                .where(
                        point.user.id.eq(userId)
                )
                .orderBy(point.createdAt.desc())
                .fetch();
    }
}
