package com.umust.dobonglife.domain.notification.service;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.domain.notification.domain.repository.NotificationRepository;
import com.umust.dobonglife.domain.notification.exception.NotificationException;
import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.firebase.NotificationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationUtil notificationUtil;

    public CursorResponse<NotificationResponse> getNotifications(Long userId, String filter, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        NotificationType type = "ALL".equals(filter) ? null : NotificationType.valueOf(filter);
        Slice<Notification> notifications = notificationRepository.findNotificationsNoOffset(userId, lastId, type, pageable);

        return CursorUtils.toCursorResponse(notifications, NotificationResponse::from);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(ErrorCode.INVALID_NOTIFICATION_ID)); // TODO: NOSUCH를 그대로 사용할지 고민

        if (!notification.getUserId().equals(userId)) {
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
    public void createNotification(Long userId, NotificationType type, String title, String content, String relatedUrl, String fcmToken) {
        Notification notification = Notification.create(userId, type, title, content, relatedUrl);
        notificationRepository.save(notification);

        if (fcmToken != null) {
            notificationUtil.sendToDevice(fcmToken, title, content, relatedUrl);
        }
    }
}
