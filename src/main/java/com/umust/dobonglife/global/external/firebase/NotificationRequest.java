package com.umust.dobonglife.global.external.firebase;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;

public record NotificationRequest(
        Long userId,
        String fcmToken,
        NotificationType type,
        String title,
        String body,
        String relatedUrl
) {}
