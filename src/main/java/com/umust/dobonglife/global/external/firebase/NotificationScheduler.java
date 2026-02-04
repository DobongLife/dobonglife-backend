package com.umust.dobonglife.global.external.firebase;

import com.umust.dobonglife.domain.coupon.domain.entity.Coupon;
import com.umust.dobonglife.domain.coupon.domain.repository.CouponRepository;
import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.repository.NotificationRepository;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.repository.ScheduleRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final ScheduleRepository scheduleRepository;
    private final NotificationUtil notificationUtil;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyNotifications() {
        LocalDate today = LocalDate.now();
        sendDailyScheduleSummary(today);
        sendCouponExpirationNotifications(today.plusDays(3));
    }
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyScheduleSummary(LocalDate today) {
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        List<Schedule> todaySchedules = scheduleRepository.findAllByStartTimeBetween(startOfDay, endOfDay);

        for (Schedule schedule : todaySchedules) {
            if (schedule.getUser().getFcmToken() != null && !schedule.getUser().getFcmToken().isEmpty()) {
                notificationUtil.sendToDevice(
                        schedule.getUser().getFcmToken(),
                        "오늘의 일정 알림",
                        "오늘은 [" + schedule.getTitle() + "] 일정이 있습니다.",
                        NotificationType.SCHEDULE,
                        schedule.getId()
                );
            }
        }
    }

    private void sendCouponExpirationNotifications(LocalDate expiryDate) {
        LocalDateTime startOfExpiryDay = expiryDate.atStartOfDay();
        LocalDateTime endOfExpiryDay = expiryDate.atTime(LocalTime.MAX);

        List<Coupon> expiringCoupons = couponRepository.findAllByIssueEndDateBetween(startOfExpiryDay, endOfExpiryDay);

        for (Coupon userCoupon : expiringCoupons) {
            Optional<User> byId = userRepository.findById(userCoupon.getUserId());
            String token = byId.get().getFcmToken();
            if (token != null && !token.isEmpty()) {
                notificationUtil.sendToDevice(
                        token,
                        "쿠폰 만료 예정 알림",
                        "[" + userCoupon.getPromotion().getTitle() + "] 쿠폰 만료가 3일 남았습니다!",
                        NotificationType.COUPON,
                        userCoupon.getId()
                );
            }
        }
    }
}
