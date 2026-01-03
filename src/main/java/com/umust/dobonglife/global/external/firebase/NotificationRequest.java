package com.umust.dobonglife.global.external.firebase;

public record NotificationRequest(
        String topic,
        String title,
        String body
) {}
