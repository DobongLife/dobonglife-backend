package com.umust.dobonglife.domain.review.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.review.domain.repository.custom.ReviewRepositoryCustom;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}
