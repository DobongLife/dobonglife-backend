package com.umust.dobonglife.domain.notification.domain.repository;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.domain.notification.domain.repository.custom.NotificationRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
    Optional<Long> countByUserIdAndIsReadFalse(Long userId);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.user.id = :userId " +
            "AND (:lastId IS NULL OR n.id < :lastId) " +
            "AND (:type IS NULL OR n.type = :type) " +
            "ORDER BY n.id DESC")
    Slice<Notification> findNotificationsNoOffset(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            @Param("type") NotificationType type,
            Pageable pageable);
}
