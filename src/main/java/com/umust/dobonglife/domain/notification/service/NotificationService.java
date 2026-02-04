package com.umust.dobonglife.domain.notification.service;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.domain.notification.domain.repository.NotificationRepository;
import com.umust.dobonglife.domain.notification.exception.NotificationException;
import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.firebase.NotificationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationUtil notificationUtil;

    public CursorResponse<NotificationResponse> getNotifications(Long userId, String filter, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        NotificationType type = null;
        if (filter != null && !"ALL".equalsIgnoreCase(filter.trim())) {
            try {
                type = NotificationType.valueOf(filter.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                type = null;
            }
        }
        Slice<Notification> notifications = notificationRepository.findNotificationsNoOffset(userId, lastId, type, pageable);

        return CursorUtils.toCursorResponse(notifications, NotificationResponse::from);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(ErrorCode.INVALID_NOTIFICATION_ID)); // TODO: NOSUCH를 그대로 사용할지 고민

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotificationException(ErrorCode.FORBIDDEN_USER_ID);
        }

        notification.markAsRead();
    }

    public boolean hasNewNotifications(Long userId) {
        Optional<Long> countOptional = notificationRepository.countByUserIdAndIsReadFalse(userId);

        Long count = countOptional.orElse(0L);
        return count > 0;
    }

    // 새로운 알림 생성
    @Transactional
    public void createNotification(User user, NotificationType type, String title, String content, Long relatedUrlId) {
        if (user.getFcmToken() == null || user.getFcmToken().isEmpty()) {
            log.info("FCM 전송 스킵: 유저 {}의 토큰이 없음", user.getId());
            return;
        }

        if (!user.isReceivedAlarm()) {
            log.info("FCM 전송 스킵: 유저 {}가 알림을 비활성화함", user.getId());
            return;
        }

        Notification notification = Notification.create(user, type, title, content, relatedUrlId);
        notificationRepository.save(notification);
        notificationUtil.sendToDevice(user.getFcmToken(), title, content, type, relatedUrlId);
    }
}
