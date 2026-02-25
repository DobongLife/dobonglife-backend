package com.umust.dobonglife.infra.firebase;

import com.umust.dobonglife.global.common.constant.NotificationType;

public record NotificationRequest(
        Long userId,
        String fcmToken,
        NotificationType type,
        String title,
        String body,
        String relatedUrl
) {}
