package com.umust.dobonglife.global.external.firebase;

import com.google.firebase.messaging.*;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationUtil {

    public void subscribeTopic(String fcmToken, String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR_TOPIC);
        }
    }

    public void unsubscribeTopic(String fcmToken, String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR_TOPIC_CANCEL);
        }
    }

    public void sendToTopic(NotificationRequest request) {
        Message message = Message.builder()
                .setTopic(request.title())
                .setNotification(Notification.builder()
                        .setTitle(request.title())
                        .setBody(request.body())
                        .build())
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR_MESSAGE);
        }
    }

    public void sendToDevice(String fcmToken, String title, String body, String relatedUrl) {
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setTtl(3600 * 1000)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setClickAction("OPEN_ACTIVITY_1") // 클릭 시 앱 열기 액션
                        .setChannelId("dobong_life_channel") // 안드로이드 8.0+ 필수 채널 ID
                        .setSound("default")
                        .build())
                .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .setCategory("NEW_MESSAGE_CATEGORY") // iOS 전용 카테고리
                        .build())
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("relatedUrl", relatedUrl != null ? relatedUrl : "")
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR_MESSAGE);
        }
    }
}
