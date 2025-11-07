package com.umust.dobonglife.domain.notification.domain.repository.custom;

import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {
    Page<Notification> searchByFilter(Long userId, String filter, Pageable pageable);
}
