package com.umust.dobonglife.domain.point.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.repository.custom.PointRepositoryCustom;
import com.umust.dobonglife.global.common.response.slice.Cursor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.umust.dobonglife.domain.point.domain.entity.QPoint.point;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Point> findMyPoint(Long userId, int size, Cursor cursor) {

        return queryFactory
                .selectFrom(point)
                .where(
                        point.user.id.eq(userId),
                        cursor == null ? null :
                                point.createdAt.lt(cursor.getCreatedAt())
                                        .or(
                                                point.createdAt.eq(cursor.getCreatedAt())
                                                        .and(point.id.lt(cursor.getId()))
                                        )
                )
                .orderBy(point.createdAt.desc(), point.id.desc())
                .limit(size + 1)
                .fetch();
    }
}
