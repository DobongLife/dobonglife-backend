package com.umust.dobonglife.global.external.firebase;

import com.google.firebase.messaging.*;
import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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

    @Async("notificationExecutor")
    public void sendToDevice(String fcmToken, String title, String body, NotificationType type, Long id) {
        AndroidConfig androidConfig = AndroidConfig.builder()
                .setTtl(3600 * 1000)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setClickAction("OPEN_ACTIVITY_1") // 클릭 시 앱 열기 액션
                        .setChannelId("dobong-default-notifications") // 안드로이드 8.0+ 필수 채널 ID
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
                .putData("id", String.valueOf(id != null ? id : 0L))
                .putData("type", type != null ? type.name() : "NONE")
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공");
        } catch (FirebaseMessagingException e) {
            log.error("FCM 전송 오류: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SERVER_ERROR_MESSAGE);
        }
    }
}
