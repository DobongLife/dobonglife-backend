package com.umust.dobonglife.domain.point.infrastructure;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.QPointResponse;
import com.umust.dobonglife.domain.point.domain.repository.custom.PointRepositoryCustom;
import com.umust.dobonglife.global.common.response.slice.Cursor;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.umust.dobonglife.domain.point.domain.entity.QPoint;

import java.util.List;

import static com.umust.dobonglife.domain.point.domain.entity.QPoint.point;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<PointResponse> findPointsByCursor(Long userId, int size, Cursor cursor) {

        // 1) size + 1개 가져오기
        List<PointResponse> results = queryFactory
                .select(new QPointResponse(
                        point.id,
                        point.reason,
                        point.amount,
                        point.pointType
                ))
                .from(point)
                .where(
                        point.user.id.eq(userId),
                        cursorCondition(cursor)
                )
                .orderBy(point.id.desc())
                .limit(size + 1)
                .fetch();

        // 2) hasNext 계산
        boolean hasNext = results.size() > size;

        // 3) content는 size개만
        List<PointResponse> content = hasNext ? results.subList(0, size) : results;

        // 4) nextCursor 생성 (보통 마지막 요소의 id)
        String nextCursor = null;
        if (hasNext && !content.isEmpty()) {
        }

        return SliceResponse.<PointResponse>builder()
                .content(content)
                .sort(QSortResponse.of(/* 정렬 정보 */))   // 없으면 null로 두거나 기본값
                .size(size)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

    private BooleanExpression cursorCondition(Cursor cursor) {
        if (cursor == null) return null;
        return point.id.lt(cursor.getLastId());
    }
}

