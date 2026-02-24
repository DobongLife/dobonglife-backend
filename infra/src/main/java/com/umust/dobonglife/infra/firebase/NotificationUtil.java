package com.umust.dobonglife.infra.firebase;

import com.google.firebase.messaging.*;
import com.umust.dobonglife.global.common.constant.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationUtil {

    @Async("notificationExecutor")
    public void subscribeTopic(String fcmToken, String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 토픽 구독 실패 - Topic: {}, Error: {}", topic, e.getMessage());
        }
    }

    @Async("notificationExecutor")
    public void unsubscribeTopic(String fcmToken, String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 토픽 구독 취소 실패 - Topic: {}, Error: {}", topic, e.getMessage());
        }
    }

    @Async("notificationExecutor")
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
            log.error("[FCM] 토픽 메시지 전송 실패 - Topic: {}, Error: {}", request.title(), e.getMessage());
        }
    }

    @Async("notificationExecutor")
    public void sendToDevice(String fcmToken, String title, String body, NotificationType type, Long id) {
        String stringId = String.valueOf(id != null ? id : 0L);
        String stringType = (type != null) ? type.name() : "NONE";

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setSound("default")
                        .setCategory("NEW_MESSAGE_CATEGORY")
                        .setContentAvailable(true)
                        .setMutableContent(true)
                        .build())
                .putCustomData("id", stringId)
                .putCustomData("type", stringType)
                .build();

        AndroidConfig androidConfig = AndroidConfig.builder()
                .setTtl(3600 * 1000)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setClickAction("OPEN_ACTIVITY_1")
                        .setChannelId("dobong-default-notifications")
                        .setSound("default")
                        .build())
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("id", stringId)
                .putData("type", stringType)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .build();

        try {
            log.info("[FCM] 전송 시도 - Token: {}, ID: {}, Type: {}", fcmToken, stringId, stringType);
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] 전송 성공 - Response: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 전송 실패 - Code: {}, Msg: {}", e.getMessagingErrorCode(), e.getMessage());
        }
    }
}
