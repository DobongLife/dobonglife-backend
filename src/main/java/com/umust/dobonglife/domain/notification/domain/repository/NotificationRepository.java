package com.umust.dobonglife.domain.notification.domain.repository;

import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.domain.notification.domain.repository.custom.NotificationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    Optional<Long> countByUserIdAndIsReadFalse(Long userId);
}
