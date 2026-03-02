package com.umust.dobonglife.global.composition.listener;

import com.umust.dobonglife.domain.user.application.UserService;
import com.umust.dobonglife.global.common.constant.NotificationType;
import com.umust.dobonglife.global.common.event.CouponIssuedEvent;
import com.umust.dobonglife.infra.firebase.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssuedNotificationListener {

    private final UserService userService;
    private final NotificationUtil notificationUtil;

    @Async("notificationExecutor")
    @EventListener
    public void handle(CouponIssuedEvent event) {
        try {
            String fcmToken = userService.getFcmToken(event.userId());
            if (fcmToken == null || fcmToken.isBlank()) {
                log.warn("FCM 토큰 없음: userId={}", event.userId());
                return;
            }

            notificationUtil.sendToDevice(
                    fcmToken,
                    "쿠폰이 발급되었습니다",
                    "포인트 교환으로 새로운 쿠폰이 발급되었어요!",
                    NotificationType.COUPON,
                    event.couponId()
            );
        } catch (Exception e) {
            log.error("쿠폰 발급 알림 전송 실패: sagaId={}, userId={}", event.sagaId(), event.userId(), e);
        }
    }
}
