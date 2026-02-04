package com.umust.dobonglife.domain.notification.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.domain.notification.domain.entity.QNotification;
import com.umust.dobonglife.domain.notification.domain.repository.custom.NotificationRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QNotification notification = QNotification.notification;

    @Override
    public Page<Notification> searchByFilter(Long userId, String filter, Pageable pageable) {

        BooleanExpression whereCondition = notification.userId.eq(userId);

        if ("UNREAD".equals(filter)) {
            whereCondition = whereCondition.and(notification.isRead.isFalse());
        } else if ("POINT".equals(filter)) {
            whereCondition = whereCondition.and(notification.type.eq(NotificationType.POINT));
        } else if ("COUPON".equals(filter)) {
            whereCondition = whereCondition.and(notification.type.eq(NotificationType.COUPON));
        } // TODO: 수정


        List<Notification> content = queryFactory
                .selectFrom(notification)
                .where(whereCondition)
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(notification.count())
                .from(notification)
                .where(whereCondition)
                .fetchOne();

        if (totalCount == null) totalCount = 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }
}
