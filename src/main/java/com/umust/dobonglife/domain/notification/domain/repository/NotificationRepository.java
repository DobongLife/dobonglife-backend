package com.umust.dobonglife.domain.notification.domain.repository;

import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
