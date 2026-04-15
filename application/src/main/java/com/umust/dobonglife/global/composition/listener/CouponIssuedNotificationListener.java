package com.umust.dobonglife.global.composition.listener;

import com.umust.dobonglife.global.port.user.UserPort;
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

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final UserPort userPort;
    private final NotificationUtil notificationUtil;

    @Async("notificationExecutor")
    @EventListener
    public void handle(CouponIssuedEvent event) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String fcmToken = userPort.getFcmToken(event.userId());
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
                return;
            } catch (Exception e) {
                log.warn("쿠폰 발급 알림 전송 실패 (시도 {}/{}): userId={}",
                        attempt, MAX_RETRIES, event.userId(), e);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        log.error("쿠폰 발급 알림 최종 실패: userId={}, couponId={}", event.userId(), event.couponId());
    }
}
