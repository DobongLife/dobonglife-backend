package com.umust.dobonglife.domain.review.infrastructure.jpa.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.domain.vo.ReviewStatus;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.umust.dobonglife.domain.review.domain.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewSummaryResponse> findReviews(TargetType targetType, Long targetId, Long lastId, int size) {
        return queryFactory
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
                        review.targetId.eq(targetId),
                        review.targetType.eq(targetType),
                        review.reviewStatus.eq(ReviewStatus.POSTED),
                        lastIdCondition(lastId)
                )
                .orderBy(review.id.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<MyReviewResponse> findMyReviews(Long userId, TargetType targetType, Long lastId, int size) {
        return queryFactory
                .select(Projections.constructor(MyReviewResponse.class,
                        review.id,
                        review.targetId,
                        Expressions.nullExpression(String.class),
                        Expressions.nullExpression(String.class),
                        review.rating,
                        review.content,
                        review.thumbnailUrl,
                        Expressions.nullExpression(List.class),
                        review.updatedAt
                ))
                .from(review)
                .where(
                        review.userId.eq(userId),
                        review.targetType.eq(targetType),
                        review.reviewStatus.eq(ReviewStatus.POSTED),
                        lastIdCondition(lastId)
                )
                .orderBy(review.id.desc())
                .limit(size + 1)
                .fetch();
    }

    private BooleanExpression lastIdCondition(Long lastId) {
        return lastId != null ? review.id.lt(lastId) : null;
    }
}
