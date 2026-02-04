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

}
