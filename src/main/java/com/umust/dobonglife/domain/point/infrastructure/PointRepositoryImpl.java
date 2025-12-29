package com.umust.dobonglife.domain.point.infrastructure;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.QPointResponse;
import com.umust.dobonglife.domain.point.domain.repository.custom.PointRepositoryCustom;
import com.umust.dobonglife.global.common.response.slice.Cursor;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.common.response.slice.SortOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.umust.dobonglife.domain.point.domain.entity.QPoint.point;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<PointResponse> findPointsByCursor(Long userId, int size, Cursor cursor, SortOrder order) {

        // 1) size + 1개 가져오기
        List<PointResponse> results = queryFactory
                .select(new QPointResponse(
                        point.id,
                        point.reason,
                        point.amount
                ))
                .from(point)
                .where(
                        point.user.id.eq(userId),
                        cursorCondition(cursor, order)
                )
                .orderBy(orderBy(order))
                .limit(size + 1)
                .fetch();

        // 2) hasNext 계산
        boolean hasNext = results.size() > size;

        // 3) content는 size개만
        List<PointResponse> content = hasNext ? results.subList(0, size) : results;

        // 4) nextCursor 생성
        String nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = String.valueOf(
                    content.get(content.size() - 1).getPointId()
            );
        }

        return SliceResponse.<PointResponse>builder()
                .content(content)
                .size(size)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    private BooleanExpression cursorCondition(Cursor cursor, SortOrder order) {
        if (cursor == null) return null;

        return order == SortOrder.DESC
                ? point.id.lt(cursor.getLastId())
                : point.id.gt(cursor.getLastId());
    }

    private OrderSpecifier<?> orderBy(SortOrder order) {
        return order == SortOrder.DESC
                ? point.id.desc()
                : point.id.asc();
    }
}

