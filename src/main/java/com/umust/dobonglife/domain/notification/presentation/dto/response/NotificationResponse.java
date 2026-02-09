package com.umust.dobonglife.domain.notification.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.Duration;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String content,
        boolean isRead,
        LocalDateTime createdAt,
        String timeAgo, // 화면에 표시될 "몇 시간 전", "2일 전" 정보
        Long relatedUrlId) implements Identifiable {
    public static NotificationResponse from(Notification notification) {
        String timeAgo = calculateTimeAgo(notification.getCreatedAt());

        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getContent(),
                notification.isRead(),
                notification.getCreatedAt(),
                timeAgo,
                notification.getRelatedUrlId()
        );
    }

    private static String calculateTimeAgo(LocalDateTime past) {
        Duration duration = Duration.between(past, LocalDateTime.now());

        long days = duration.toDays();
        if (days > 0) {
            return days + "일 전";
        }
        long hours = duration.toHours();
        if (hours > 0) {
            return hours + "시간 전";
        }
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + "분 전";
        }

        return "방금 전";
    }

    @JsonIgnore
    @Override
    public Long getId() {
        return id;
    }
}
