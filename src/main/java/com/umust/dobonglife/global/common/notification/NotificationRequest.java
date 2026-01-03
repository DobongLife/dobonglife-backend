package com.umust.dobonglife.global.common.notification;

public record NotificationRequest(
        String topic,
        String title,
        String body
) {}
