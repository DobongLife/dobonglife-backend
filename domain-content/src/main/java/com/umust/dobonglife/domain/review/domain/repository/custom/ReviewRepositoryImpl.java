package com.umust.dobonglife.domain.review.domain.repository.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.domain.vo.ReviewStatus;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.umust.dobonglife.domain.review.domain.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ReviewSummaryResponse> findReviewsByPlaceId(Long placeId, Long lastId, int size) {
        List<ReviewSummaryResponse> content = queryFactory
                .select(Projections.constructor(ReviewSummaryResponse.class,
                        review.id,
                        review.userId,
                        review.rating,
                        review.content,
                        review.thumbnailUrl,
                        review.createdAt
                ))
                .from(review)
                .where(
                        review.targetId.eq(placeId),
                        review.targetType.eq(TargetType.PLACE),
                        review.reviewStatus.eq(ReviewStatus.POSTED),
                        lastIdCondition(lastId)
                )
                .orderBy(review.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content = content.subList(0, size);
        }

        return new SliceImpl<>(content, PageRequest.of(0, size), hasNext);
    }

    private BooleanExpression lastIdCondition(Long lastId) {
        return lastId != null ? review.id.lt(lastId) : null;
    }
}
